package com.linkshortener.service;

import com.linkshortener.dao.GroupDao;
import com.linkshortener.dao.UserDao;
import com.linkshortener.entity.Group;
import com.linkshortener.entity.User;
import com.linkshortener.enums.UserGroup;
import com.linkshortener.exception.UserAlreadyExistException;
import com.linkshortener.exception.UserNotFoundException;
import com.linkshortener.security.AuthenticationResponse;
import com.linkshortener.security.CustomUserDetails;
import com.linkshortener.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This class consist of methods that make business logic of creation,
 * retrieving, removing of users
 *
 * @author Bohdan Koval
 * @see UserDao
 * @see GroupDao
 * @see UserGroup
 * @see User
 * @see JwtService
 */
@Service
@Transactional(readOnly = true)
public class UserService {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserDao userDao;
    private final GroupDao groupDao;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserDao userDao, GroupDao groupDao, JwtService jwtService, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userDao = userDao;
        this.groupDao = groupDao;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Return all users to user with ADMIN authorities
     *
     * @return all users
     */
    public List<User> getAllUsers() {
        return userDao.getAll();
    }

    /**
     * Returns user by email, if not found returns empty optional.
     *
     * @param email user email
     * @return found user, empty optional
     */
    public Optional<User> getUserByEmail(String email) {
        if (userDao.getByUsername(email).isPresent()) {
            return Optional.of(userDao.getByUsername(email).get());
        }

        LOGGER.warn("User with email: {} was not found", email);

        return Optional.empty();
    }

    /**
     * Adds new user if the username was not already used.
     *
     * @param user      the user to add
     * @param userGroup authorities that user will have USER or ADMIN
     * @throws UserAlreadyExistException if user with this email already exist
     */
    @Transactional
    public void addUser(User user, UserGroup userGroup) {
        Optional<User> foundedUser = userDao.getByUsername(user.getEmail());

        if (foundedUser.isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            addGroupToUser(user, userGroup);
            userDao.save(user);
            return;
        }

        throw new UserAlreadyExistException(user.getEmail());
    }

    /**
     * Adds group to user that will grant him authorities
     *
     * @param user      the user that will get a new group
     * @param userGroup authorities that user will have USER or ADMIN
     */
    public void addGroupToUser(User user, UserGroup userGroup) {
        Set<Group> groups = new HashSet<>();

        if (user.getRoles() != null) {
            groups.addAll(user.getRoles());
        }

        String role = userGroup.toString();

        LOGGER.info("Adding user with groups :{} and Role :{}", groups, groupDao.getByCode(role).orElse(new Group("Not found", "")));
        groupDao.getByCode(role).ifPresent(groups::add);
        user.setRoles(groups);
    }

    /**
     * Return registration response with jwt token that will be used to authorization
     *
     * @param user logged or registered user
     * @return jwt token to make authorized requests
     * @throws UserNotFoundException if not found user with this email and password
     */
    public AuthenticationResponse getRegistrationResponse(User user) {
        String jwt;

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        } catch (AuthenticationException e) {
            throw new UserNotFoundException(user.getEmail());
        }

        UserDetails userDetails = new CustomUserDetails(user.getEmail(), user.getPassword());
        jwt = jwtService.generateJwt(new HashMap<>(), userDetails);

        return new AuthenticationResponse(jwt);
    }

    /**
     * Removes all users to user with ADMIN authorities
     */
    @Transactional
    public void removeAllUsers() {
        userDao.deleteAll();
    }

    /**
     * Removes user by id if not found no exception
     *
     * @param id the id of user
     */
    @Transactional
    public void removeUser(Long id) {
        userDao.get(id).ifPresent(userDao::delete);
    }
}
