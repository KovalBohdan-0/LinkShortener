package com.example.linkshortener.service;

import com.example.linkshortener.dao.GroupDao;
import com.example.linkshortener.dao.UserDao;
import com.example.linkshortener.entity.Group;
import com.example.linkshortener.entity.User;
import com.example.linkshortener.security.JwtService;
import com.example.linkshortener.security.CustomUserDetails;
import com.example.linkshortener.security.AuthenticationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserDao userDao;
    private final GroupDao groupDao;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final static Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    public enum UserGroup {
            USER,
            ADMIN
    }

    public UserService(UserDao userDao, GroupDao groupDao, JwtService jwtService, BCryptPasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.groupDao = groupDao;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return  userDao.getAll();
    }

    public User getUserByEmail(String email) {
        return userDao.getByUsername(email);
    }

    @Transactional
    public boolean addUser(User user, UserGroup userGroup) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        addGroupToUser(user, userGroup);
        userDao.save(user);

        return true;
    }

    public void addGroupToUser(User user, UserGroup group) {
        Set<Group> groups = new HashSet<>();

        if (user.getRoles() != null) {
            groups.addAll(user.getRoles());
        }

        String role = group.toString();

        LOGGER.info("Adding user with groups: " + groups + " and Role:" + groupDao.getByCode(role).orElse(new Group("Not found", "")));
        groupDao.getByCode(role).ifPresent(groups::add);
        user.setRoles(groups);
    }

    public AuthenticationResponse getRegistrationResponse(User user) {
        UserDetails userDetails = new CustomUserDetails(user.getEmail(), user.getPassword());
        String jwt = jwtService.generateJwt(new HashMap<>(), userDetails);

        return new AuthenticationResponse(jwt);
    }

    @Transactional
    public void removeAllUsers() {
        getAllUsers().forEach(user -> removeUser(user.getId()));
    }

    @Transactional
    public void removeUser(Long id) {
        userDao.get(id).ifPresent(userDao::delete);
    }
}
