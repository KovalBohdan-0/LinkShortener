package com.linkshortener.controller;

import com.linkshortener.dto.UserDto;
import com.linkshortener.entity.User;
import com.linkshortener.enums.UserGroup;
import com.linkshortener.security.AuthenticationResponse;
import com.linkshortener.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Validated
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
    public List<UserDto> getUsers() {
        return userService.getAllUsers().stream().map(this::convertToUserDto).collect(Collectors.toList());
    }

    @PostMapping("/users")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody UserDto userDto) {
        User user = userService.getUserByEmail(userDto.getEmail());

        if (user == null) {
            userService.addUser(convertToUser(userDto), UserGroup.USER);
            return new ResponseEntity<>(userService.getRegistrationResponse(convertToUser(userDto)), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PostMapping("/users/admin")
    public ResponseEntity<AuthenticationResponse> registerAdmin(@Valid @RequestBody UserDto userDto) {
        User user = userService.getUserByEmail(userDto.getEmail());

        if (user == null) {
            userService.addUser(convertToUser(userDto), UserGroup.ADMIN);

            return new ResponseEntity<>(userService.getRegistrationResponse(convertToUser(userDto)), HttpStatus.OK);
        }


        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody UserDto userDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword()));
        User foundedUser = userService.getUserByEmail(userDto.getEmail());

        return userService.getRegistrationResponse(foundedUser);
    }

    @DeleteMapping("/users")
    public void deleteUsers() {
        userService.removeAllUsers();
    }

    private UserDto convertToUserDto(User user) {
        return new UserDto(user.getEmail(), user.getPassword());
    }

    private User convertToUser(UserDto userDto) {
        return new User(userDto.getEmail(), userDto.getPassword());
    }
}
