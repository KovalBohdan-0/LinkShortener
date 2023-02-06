package com.linkshortener.service;

import com.linkshortener.dao.GroupDao;
import com.linkshortener.dao.UserDao;
import com.linkshortener.entity.User;
import com.linkshortener.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserDao userDao;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private GroupDao groupDao;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        userService = new UserService(userDao, groupDao, jwtService, passwordEncoder);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldAddUser() {
        User user = new User();
        user.setPassword("pass");

        boolean isUserCreated = userService.addUser(user, UserService.UserGroup.USER);

        verify(userDao).save(user);
        verify(passwordEncoder).encode("pass");
        assertThat(isUserCreated).isTrue();
    }

    @Test
    void shouldFailToAddExistingUser() {

    }

    @Test
    void shouldGetAllUsers() {
        userService.getAllUsers();

        verify(userDao).getAll();
    }

    @Test
    void shouldGetUserByEmail() {
        userService.getUserByEmail("");

        verify(userDao).getByUsername("");
    }

    @Test
    void shouldRemoveUserById() {
        User user = new User();
        when(userDao.get(1L)).thenReturn((Optional<User>) Optional.of(user));
        userService.removeUser(1L);

        verify(userDao).get(1L);
        verify(userDao).delete(user);
    }

    @Test
    void shouldRemoveAllUsers() {
        List<User> users = new ArrayList<>(List.of(new User()));
        when(userDao.getAll()).thenReturn(users);
        userService.removeAllUsers();

        verify(userDao).getAll();
        verify(userDao).delete(new User());
    }
}