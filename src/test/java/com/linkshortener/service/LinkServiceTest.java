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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


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
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Mock
    private Optional<User> optionalUser;
    @Mock
    private Link link;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        linkService = new LinkService(linkDao, userDao);
        SecurityContextHolder.setContext(securityContext);
        when(userDao.getByUsername(authentication.getName())).thenReturn(optionalUser);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldGetLinkById() {
        User user = new User();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userDao.getByUsername(any())).thenReturn(Optional.of(user));
        when(link.getUser()).thenReturn(user);
        when(linkDao.get(anyLong())).thenReturn(Optional.of(link));

        boolean founded = linkService.getLinkById(anyLong()).isPresent();

        verify(linkDao).get(anyLong());
        assertThat(founded).isTrue();
    }

    @Test
    void shouldGetLinkByAlias() {
        User user = new User();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userDao.getByUsername(any())).thenReturn(Optional.of(user));
        when(link.getUser()).thenReturn(user);
        when(linkDao.getLinkByAlias(anyString())).thenReturn(Optional.of(link));

        boolean founded = linkService.getLinkByAlias(anyString()).isPresent();

        verify(linkDao).getLinkByAlias(anyString());
        assertThat(founded).isTrue();
    }

    @Test
    void shouldGetAllLinks() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(optionalUser.isPresent()).thenReturn(true);
        when(optionalUser.get()).thenReturn(user);

        linkService.getAllLinks();

        verify(user).getLinks();
    }

    @Test
    void shouldNotGetAllLinksWhenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);

        linkService.getAllLinks();

        verify(user, never()).getLinks();
    }

    @Test
    void shouldAddLink() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(optionalUser.isPresent()).thenReturn(true);
        when(link.getUser()).thenReturn(user);

        linkService.addLink(link);

        verify(linkDao).save(any(Link.class));
    }

    @Test
    void shouldRemoveLink() {
        User user = new User();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userDao.getByUsername(any())).thenReturn(Optional.of(user));
        when(link.getUser()).thenReturn(user);
        when(linkDao.get(anyLong())).thenReturn(Optional.of(link));

        linkService.removeLink(0L);

        verify(linkDao).delete(any(Link.class));
    }

    @Test
    void shouldRemoveAllLinks() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(optionalUser.isPresent()).thenReturn(true);
        when(optionalUser.get()).thenReturn(user);
        when(user.getEmail()).thenReturn("email");

        linkService.removeAllLinks();

        verify(linkDao).deleteAllByUserId(anyLong());
    }

    @Test
    void shouldNotRemoveAllLinksWhenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);

        linkService.removeAllLinks();

        verify(linkDao, never()).deleteAll();
    }
}