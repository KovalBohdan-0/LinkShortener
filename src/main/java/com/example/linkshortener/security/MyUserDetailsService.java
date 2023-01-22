package com.example.linkshortener.security;

import com.example.linkshortener.dao.UserDao;
import com.example.linkshortener.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@Configuration
public class MyUserDetailsService implements UserDetailsService {
    private final UserDao userDao;
    private final static Logger LOGGER = LoggerFactory.getLogger(UserDao.class);

    public MyUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.getByUsername(username);
        LOGGER.info("Loaded user with email:"  + user.toString());
        return new MyUserDetails(username, user.getPassword());
    }
}
