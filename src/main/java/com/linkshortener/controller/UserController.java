package com.linkshortener.controller;

import com.linkshortener.entity.User;
import com.linkshortener.enums.UserGroup;
import com.linkshortener.security.AuthenticationResponse;
import com.linkshortener.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final Logger LOGGER =  LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/users")
    public AuthenticationResponse register(@RequestBody User user) {
        userService.addUser(user, UserGroup.USER);

        return userService.getRegistrationResponse(user);
    }

    @PostMapping("/users/admin")
    public AuthenticationResponse registerAdmin(@RequestBody User user) {
        userService.addUser(user, UserGroup.ADMIN);

        return userService.getRegistrationResponse(user);
    }

    @DeleteMapping("/users")
    public void deleteUsers() {
        userService.removeAllUsers();
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody User user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        User foundedUser = userService.getUserByEmail(user.getEmail());
        return userService.getRegistrationResponse(foundedUser);
    }
}
