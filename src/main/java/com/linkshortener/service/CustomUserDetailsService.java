package com.linkshortener.service;

import com.linkshortener.repository.GroupRepository;
import com.linkshortener.repository.UserRepository;
import com.linkshortener.entity.Group;
import com.linkshortener.entity.User;
import com.linkshortener.security.CustomUserDetails;
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
    private final static Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public CustomUserDetailsService(UserRepository userRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (userRepository.findByEmail(username).isPresent()) {
            User user = userRepository.findByEmail(username).get();

            return new CustomUserDetails(username, user.getPassword(), new HashSet<>(getAuthorities(user)));
        }

        LOGGER.error("User with email: {} was not found", username);
        throw new UsernameNotFoundException(String.format("User with email: %s was not found", username));
    }

    private Collection<GrantedAuthority> getAuthorities(User user) {
        Set<Group> userGroups = groupRepository.findGroupByUsersId(user.getId());
        Collection<GrantedAuthority> authorities = new ArrayList<>(userGroups.size());

        for (Group userGroup : userGroups) {
            authorities.add(new SimpleGrantedAuthority(userGroup.getCode().toUpperCase()));
        }

        return authorities;
    }
}