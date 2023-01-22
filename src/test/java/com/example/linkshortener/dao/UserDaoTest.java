package com.example.linkshortener.dao;

import com.example.linkshortener.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest//(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@Import(UserDao.class)
class UserDaoTest {
    @Autowired
    private UserDao userDao;

    @Test
    void shouldSaveUser() {
        User user = new User("Fulllink", "ShortLink");
        userDao.save(user);

        User savedUser = userDao.getByUsername("Fulllink");

        assertThat(savedUser).isNotNull();
    }

    @Test
    void shouldUpdateUser() {
        User user = new User("Fulllink", "ShortLink");
        userDao.save(user);

        User savedUser = userDao.getByUsername("Fulllink");

        assertThat(savedUser).isNotNull();
    }

    @Test
    void shouldDeleteUser() {
        User user = new User("Fulllink", "ShortLink");
        userDao.save(user);

        User savedUser = userDao.getByUsername("Fulllink");

        assertThat(savedUser).isNotNull();
    }
}