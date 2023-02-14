package com.linkshortener.service;

import com.linkshortener.dao.GroupDao;
import com.linkshortener.dao.UserDao;
import com.linkshortener.entity.Group;
import com.linkshortener.entity.User;
import com.linkshortener.enums.UserGroup;
import com.linkshortener.security.AuthenticationResponse;
import com.linkshortener.security.CustomUserDetails;
import com.linkshortener.security.JwtService;
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
    private final static Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserDao userDao;
    private final GroupDao groupDao;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserDao userDao, GroupDao groupDao, JwtService jwtService, BCryptPasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.groupDao = groupDao;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userDao.getAll();
    }

    public User getUserByEmail(String email) {
        return userDao.getByUsername(email);
    }

    @Transactional
    public boolean addUser(User user, UserGroup userGroup) {
        if (userDao.getByUsername(user.getEmail()) != null) {
            return false;
        }

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

        LOGGER.info("Adding user with groups :{} and Role :{}", groups, groupDao.getByCode(role).orElse(new Group("Not found", "")));
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
        userDao.deleteAll();
    }

    @Transactional
    public void removeUser(Long id) {
        userDao.get(id).ifPresent(userDao::delete);
    }
}
