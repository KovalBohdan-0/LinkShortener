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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class consist of api endpoints that will create user or can create
 * account with ADMIN authorities if signed as admin. Also gives ability to
 * log in to existing account.
 *
 * @author Bohdan Koval
 * @see UserService
 * @see User
 * @see UserDto
 * @see UserGroup
 * @see AuthenticationResponse
 */
@Validated
@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * If user has ADMIN authorities than it wil return all users. If user not
     * authenticated than the result will be empty array.
     *
     * @return list of all users, HTTP status code
     * 403 - forbidden for user without ADMIN authority
     */
    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userService.getAllUsers().stream().map(this::convertToUserDto).collect(Collectors.toList());
    }

    /**
     * Creates user with USER authorities, fails if email is already used.
     * Usage:
     * <pre>
     * {
     *   "email": "user@gmail.com",
     *   "password": "password"
     * }
     * </pre>
     *
     * @param userDto user to add
     * @return HTTP status code
     * 200 - user added,
     * 400 - not valid user,
     * 409 - email already exist
     */
    @PostMapping("/users")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody UserDto userDto) {
        Optional<User> user = userService.getUserByEmail(userDto.getEmail());

        if (user.isEmpty()) {
            userService.addUser(convertToUser(userDto), UserGroup.USER);
            return new ResponseEntity<>(userService.getRegistrationResponse(convertToUser(userDto)), HttpStatus.OK);
        }

        LOGGER.warn("User with email :{} already exist", userDto.getEmail());
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    /**
     * Creates user with ADMIN authorities, fails if email is already used
     * and user doesn't have ADMIN authorities.
     * Usage:
     * <pre>
     * {
     *   "email": "admin@gmail.com",
     *   "password": "password"
     * }
     * </pre>
     *
     * @param userDto user to add
     * @return HTTP status code
     * 200 - user added,
     * 400 - not valid user,
     * 403 - forbidden for user without ADMIN authority
     * 409 - email already exist
     */
    @PostMapping("/users/admin")
    public ResponseEntity<AuthenticationResponse> registerAdmin(@Valid @RequestBody UserDto userDto) {
        Optional<User> user = userService.getUserByEmail(userDto.getEmail());

        if (user.isEmpty()) {
            userService.addUser(convertToUser(userDto), UserGroup.ADMIN);

            return new ResponseEntity<>(userService.getRegistrationResponse(convertToUser(userDto)), HttpStatus.OK);
        }

        LOGGER.warn("User with email :{} already exist", userDto.getEmail());
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    /**
     * Logins and returns jwt token for authorization
     *
     * @param userDto user to add
     * @return jwt token as success, HTTP status code
     * 200 - login was successful
     * 400 - not valid user
     * 404 - user or password incorrect
     */
    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody UserDto userDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword()));
        Optional<User> foundedUser = userService.getUserByEmail(userDto.getEmail());

        if (foundedUser.isPresent()) {
            return userService.getRegistrationResponse(foundedUser.get());
        }

        throw new UsernameNotFoundException("User not found, email :" + userDto.getEmail());
    }

    /**
     * Delete all users if signed as admin
     * Returns HTTP status code
     * 403 - forbidden for user without ADMIN authority
     */
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
