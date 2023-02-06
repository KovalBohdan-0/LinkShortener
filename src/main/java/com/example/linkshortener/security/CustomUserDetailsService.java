package com.example.linkshortener.security;

import com.example.linkshortener.dao.GroupDao;
import com.example.linkshortener.dao.UserDao;
import com.example.linkshortener.entity.Group;
import com.example.linkshortener.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserDao.class);
    private final UserDao userDao;
    private final GroupDao groupDao;

    public CustomUserDetailsService(UserDao userDao, GroupDao groupDao) {
        this.userDao = userDao;
        this.groupDao = groupDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.getByUsername(username);
        LOGGER.info("Loaded user with email:" + user.toString() + " and Authorities :" + getAuthorities(user));

        return new CustomUserDetails(username, user.getPassword(), new HashSet<>(getAuthorities(user)));
    }

    private Collection<GrantedAuthority> getAuthorities(User user) {
        Set<Group> userGroups = groupDao.getGroupsByUserId(user.getId());
        Collection<GrantedAuthority> authorities = new ArrayList<>(userGroups.size());

        for (Group userGroup : userGroups) {
            authorities.add(new SimpleGrantedAuthority(userGroup.getCode().toUpperCase()));
        }

        return authorities;
    }
}