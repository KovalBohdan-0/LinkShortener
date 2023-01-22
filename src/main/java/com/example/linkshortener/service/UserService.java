package com.example.linkshortener.service;

import com.example.linkshortener.dao.Dao;
import com.example.linkshortener.dao.UserDao;
import com.example.linkshortener.entity.User;
import com.example.linkshortener.security.JwtService;
import com.example.linkshortener.security.MyUserDetails;
import com.example.linkshortener.security.AuthenticationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {
    private UserDao userDao;
    private final JwtService jwtService;
    private final static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    public UserService(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    public List<User> getAllUsers() {
        return  userDao.getAll();
    }

    public User getUserByEmail(String email) {
        return userDao.getByUsername(email);
    }

    @Transactional
    public void addUser(User user) {
        userDao.save(user);
    }

    public AuthenticationResponse getRegistrationResponse(User user) {
        UserDetails userDetails = new MyUserDetails(user.getEmail(), user.getPassword());
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
