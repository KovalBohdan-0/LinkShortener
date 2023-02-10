package com.linkshortener.service;

import com.linkshortener.dao.LinkDao;
import com.linkshortener.dao.UserDao;
import com.linkshortener.entity.Link;
import com.linkshortener.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.mockito.Mockito.*;


class LinkServiceTest {
    @InjectMocks
    private LinkService linkService;
    @Mock
    private LinkDao linkDao;
    @Mock
    private UserDao userDao;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private User user;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        linkService = new LinkService(linkDao, userDao);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldGetAllLinks() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userDao.getByUsername(authentication.getName())).thenReturn(user);
        linkService.getAllLinks();

        verify(userDao).getByUsername(any());
        verify(user).getLinks();
    }

    @Test
    void shouldNotGetAllLinksWhenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);
        linkService.getAllLinks();

        verify(userDao, never()).getByUsername(any());
        verify(user, never()).getLinks();
    }

    @Test
    void shouldAddLink() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userDao.getByUsername(authentication.getName())).thenReturn(user);
        linkService.addLink(new Link("link", "shortLink"));

        verify(userDao).getByUsername(any());
        verify(linkDao).save(any(Link.class));
    }

    @Test
    void shouldRemoveLink() {
        when(linkDao.get(anyLong())).thenReturn(Optional.of(new Link()));
        linkService.removeLink(1L);

        verify(linkDao).delete(any(Link.class));
    }

    @Test
    void shouldRemoveAllLinks() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userDao.getByUsername(authentication.getName())).thenReturn(user);
        linkService.removeAllLinks();

        verify(userDao).getByUsername(any());
        verify(linkDao).deleteAllByUserId(anyLong());
    }

    @Test
    void shouldNotRemoveAllLinksWhenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);
        linkService.removeAllLinks();

        verify(linkDao, never()).deleteAll();
    }
}