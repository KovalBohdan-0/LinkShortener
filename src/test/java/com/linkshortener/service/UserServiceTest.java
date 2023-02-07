package com.linkshortener.service;

import com.linkshortener.dao.GroupDao;
import com.linkshortener.dao.UserDao;
import com.linkshortener.entity.User;
import com.linkshortener.enums.UserGroup;
import com.linkshortener.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

        boolean isUserCreated = userService.addUser(user, UserGroup.USER);

        verify(userDao).save(user);
        verify(passwordEncoder).encode("pass");
        assertThat(isUserCreated).isTrue();
    }

    @Test
    void shouldFailToAddExistingUser() {
        User user = new User();
        user.setEmail("email");
        when(userDao.getByUsername(user.getEmail())).thenReturn(user);
        userService.addUser(user, UserGroup.USER);

        verify(userDao, never()).save(user);
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
        when(userDao.get(1L)).thenReturn(Optional.of(user));
        userService.removeUser(1L);

        verify(userDao).get(1L);
        verify(userDao).delete(user);
    }

    @Test
    void shouldFailToRemoveNotExistingUser() {
        when(userDao.get(1L)).thenReturn(Optional.empty());
        userService.removeUser(1L);

        verify(userDao).get(1L);
        verify(userDao, never()).delete(new User());
    }

    @Test
    void shouldRemoveAllUsers() {
        userDao.deleteAll();

        verify(userDao).deleteAll();
    }
}