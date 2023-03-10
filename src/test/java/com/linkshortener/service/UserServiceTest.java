package com.linkshortener.service;

import com.linkshortener.dao.GroupDao;
import com.linkshortener.dao.UserDao;
import com.linkshortener.entity.User;
import com.linkshortener.enums.UserGroup;
import com.linkshortener.exception.UserAlreadyExistException;
import com.linkshortener.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private AuthenticationManager authenticationManager;
    @Mock
    private GroupDao groupDao;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        userService = new UserService(userDao, groupDao, jwtService, passwordEncoder, authenticationManager);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldAddUser() {
        userService.addUser(new User("email", "pass"), UserGroup.USER);

        verify(userDao).save(any(User.class));
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    void shouldFailToAddExistingUser() {
        User user = new User("user", "pass");
        when(userDao.getByUsername(anyString())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistException.class, () -> userService.addUser(user, UserGroup.USER));
    }

    @Test
    void shouldGetAllUsers() {
        userService.getAllUsers();

        verify(userDao).getAll();
    }

    @Test
    void shouldGetUserByEmail() {
        when(userDao.getByUsername(anyString())).thenReturn(Optional.of(new User()));

        userService.getUserByEmail(anyString());

        verify(userDao, atLeastOnce()).getByUsername(anyString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldGetRegistrationResponse() {
        userService.getAuthenticationResponse(new User());

        verify(jwtService).generateJwt(any(Map.class), any(UserDetails.class));
    }

    @Test
    void shouldRemoveUserById() {
        when(userDao.get(1L)).thenReturn(Optional.of(new User()));

        userService.removeUser(1L);

        verify(userDao).get(anyLong());
        verify(userDao).delete(any(User.class));
    }

    @Test
    void shouldFailToRemoveNotExistingUser() {
        when(userDao.get(1L)).thenReturn(Optional.empty());

        userService.removeUser(1L);

        verify(userDao).get(anyLong());
        verify(userDao, never()).delete(any(User.class));
    }

    @Test
    void shouldRemoveAllUsers() {
        userService.removeAllUsers();

        verify(userDao).deleteAll();
    }
}