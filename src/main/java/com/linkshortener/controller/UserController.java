package com.linkshortener.controller;

import com.linkshortener.dto.UserDto;
import com.linkshortener.entity.User;
import com.linkshortener.enums.UserGroup;
import com.linkshortener.security.AuthenticationResponse;
import com.linkshortener.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
@CrossOrigin
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @Operation(summary = "If user has ADMIN authorities than it wil return all users. If user not authenticated than the result will be empty array.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "users returned",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "403", description = "forbidden for user without ADMIN authority")
    })
    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userService.getAllUsers().stream().map(this::convertToUserDto).collect(Collectors.toList());
    }

    @Operation(summary = "Creates user with USER authorities, fails if email is already used.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "user added",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "not valid user"),
            @ApiResponse(responseCode = "409", description = "email already exist")
    })
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

    @Operation(summary = "Creates user with ADMIN authorities, fails if email is already used and user doesn't have ADMIN authorities.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "user added",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "not valid user"),
            @ApiResponse(responseCode = "403", description = "forbidden for user without ADMIN authority"),
            @ApiResponse(responseCode = "409", description = "email already exist")
    })
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

    @Operation(summary = "Logins and returns jwt token for authorization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "login was successful",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "not valid user"),
            @ApiResponse(responseCode = "404", description = "user or password incorrect")
    })
    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody UserDto userDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword()));
        Optional<User> foundedUser = userService.getUserByEmail(userDto.getEmail());

        if (foundedUser.isPresent()) {
            return userService.getRegistrationResponse(foundedUser.get());
        }

        throw new UsernameNotFoundException("User not found, email :" + userDto.getEmail());
    }

    @Operation(summary = "Delete all users if signed as admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "removed all users"),
            @ApiResponse(responseCode = "403", description = "forbidden for user without ADMIN authority")
    })
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
