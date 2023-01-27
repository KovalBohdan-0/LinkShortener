package com.example.linkshortener.dao;

import com.example.linkshortener.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(UserDao.class)
class UserDaoTest {
    @Autowired
    private UserDao userDao;

    @BeforeEach
    @Rollback(false)
    void setUp() {
        userDao.save(new User("first@gmail.com", "pass"));
    }

    @Test
    void shouldGetUserById() {
        Optional<User> user = userDao.get(1);

        boolean isFound = user.isPresent();

        assertThat(isFound).isTrue();
    }

    @Test
    void shouldGetUserByUsername() {
        User user = userDao.getByUsername("first@gmail.com");
        User notExsitingUser = userDao.getByUsername("first1@gmail.com");

        assertThat(user).isNotNull();
        assertThat(notExsitingUser).isNull();
    }

    @Test
    void shouldGetAllUsers() {
        userDao.save(new User("second@gmail.com", "pass"));
        List<User> users = userDao.getAll();

        boolean containTwoUsers = users.size() == 2;

        assertThat(containTwoUsers).isTrue();
    }

    @Test
    void shouldSaveUser() {
        User user = new User("test@gmail.com", "pass");
        userDao.save(user);

        User savedUser = userDao.getByUsername("test@gmail.com");

        assertThat(savedUser).isNotNull();
    }

    @Test
    void shouldUpdateUser() {
        User user = userDao.getByUsername("first@gmail.com");
        user.setEmail("updated@gmail.com");
        userDao.update(user);

        User previousUser = userDao.getByUsername("first@gmail.com");
        User updatedUser = userDao.getByUsername("updated@gmail.com");

        assertThat(updatedUser).isNotNull();
        assertThat(previousUser).isNull();
    }

    @Test
    void shouldDeleteUser() {
        User user = userDao.getByUsername("first@gmail.com");
        userDao.delete(user);

        User deletedUser = userDao.getByUsername("first@gmail.com");

        assertThat(deletedUser).isNull();
    }
}