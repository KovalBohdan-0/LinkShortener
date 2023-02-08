package com.linkshortener.service;

import com.linkshortener.dao.LinkDao;
import com.linkshortener.dao.UserDao;
import com.linkshortener.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


class LinkServiceTest {
    @InjectMocks
    private LinkService linkService;
    @Mock
    private LinkDao linkDao;
    @Mock
    private UserDao userDao;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        linkService = new LinkService(linkDao, userDao);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void getAllLinks() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        // Create mock Authentication
        Authentication authentication = Mockito.mock(Authentication.class);

        // Set authentication in the SecurityContext
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        // Set SecurityContext in the SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);
//        User user = Mockito.mock(User.class);
//        when(user.getLinks()).thenReturn(any(Set.class));

        linkService.getAllLinks();

        verify(userDao).getByUsername(anyString());
    }

    private SecurityContext getDesiredSecurityContext(String authority) {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(authority));
        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new TestingAuthenticationToken(null, null, String.valueOf(authorities)));
        return securityContext;
    }
}