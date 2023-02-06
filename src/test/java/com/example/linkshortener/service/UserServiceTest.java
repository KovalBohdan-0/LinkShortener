package com.example.linkshortener.service;

import com.example.linkshortener.dao.GroupDao;
import com.example.linkshortener.dao.UserDao;
import com.example.linkshortener.entity.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;
    @MockBean
    private UserDao userDao;
    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void shouldAddUser() {
        User user = new User();
        user.setPassword("pass");

        boolean isUserCreated = userService.addUser(user, UserService.UserGroup.USER);

        Mockito.verify(userDao).save(user);
        Mockito.verify(passwordEncoder).encode("pass");
        assertThat(isUserCreated).isTrue();
    }

    @Test
    void shouldFailToAddExistingUser() {

    }
}