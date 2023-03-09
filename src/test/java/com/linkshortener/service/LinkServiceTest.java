package com.linkshortener.service;

import com.linkshortener.dao.LinkDao;
import com.linkshortener.dao.UserDao;
import com.linkshortener.entity.Link;
import com.linkshortener.entity.User;
import com.linkshortener.exception.LinkAlreadyExistException;
import com.linkshortener.exception.LinkNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void shouldNotGetLinkByIdWhenNotFound() {
        User user = new User();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userDao.getByUsername(any())).thenReturn(Optional.of(user));
        when(link.getUser()).thenReturn(user);
        when(linkDao.get(anyLong())).thenReturn(Optional.empty());

        boolean founded = linkService.getLinkById(anyLong()).isPresent();

        verify(linkDao).get(anyLong());
        assertThat(founded).isFalse();
    }

    @Test
    void shouldGetUsersLinkByAlias() {
        User user = new User();
        user.setId(1L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userDao.getByUsername(any())).thenReturn(Optional.of(user));
        when(link.getUser()).thenReturn(user);
        when(linkDao.getUsersLinkByAlias("alias", 1L)).thenReturn(Optional.of(link));

        boolean founded = linkService.getUsersLinkByAlias("alias") != null;

        verify(linkDao, atLeastOnce()).getUsersLinkByAlias("alias", 1L);
        assertThat(founded).isTrue();
    }

    @Test
    void shouldThrowNotFoundWhenGettingLinkByNotFoundAlias() {
        User user = new User();
        user.setId(1L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userDao.getByUsername(any())).thenReturn(Optional.of(user));
        when(linkDao.getUsersLinkByAlias("alias", 1L)).thenReturn(Optional.empty());

        assertThrows(LinkNotFoundException.class, () -> linkService.getUsersLinkByAlias("alias"));
    }

    @Test
    void shouldGetAllLinks() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(optionalUser.isPresent()).thenReturn(true);
        when(userDao.getByUsername(authentication.getName())).thenReturn(optionalUser);
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
        User user = new User();
        user.setId(1L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(optionalUser.isPresent()).thenReturn(true);
        when(userDao.getByUsername(authentication.getName())).thenReturn(Optional.of(user));
        when(link.getUser()).thenReturn(user);

        linkService.addLink(link);

        verify(linkDao).save(any(Link.class));
    }

    @Test
    void shouldNotAddLinkWhenAlreadyExist() {
        User user = new User();
        user.setId(1L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(optionalUser.isPresent()).thenReturn(true);
        when(linkDao.getUsersLinkByAlias("alias", 1L)).thenReturn(Optional.of(link));
        when(userDao.getByUsername(authentication.getName())).thenReturn(Optional.of(user));
        when(link.getUser()).thenReturn(user);


        assertThrows(LinkAlreadyExistException.class, () -> linkService.addLink(new Link("fullLink", "alias")));
    }

    @Test
    void shouldAddLinkView() {
        User user = new User();
        user.setId(1L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(linkDao.getUsersLinkByAlias("alias", 1L)).thenReturn(Optional.of(link));
        when(userDao.getByUsername(authentication.getName())).thenReturn(Optional.of(user));

        linkService.addLinkView("alias");

        verify(link).setViews(anyLong());
    }

    @Test
    void shouldNotAddLinkViewWhenNotFound() {
        User user = new User();
        user.setId(1L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(linkDao.getUsersLinkByAlias("alias", 1L)).thenReturn(Optional.empty());
        when(userDao.getByUsername(authentication.getName())).thenReturn(Optional.of(user));

        assertThrows(LinkNotFoundException.class, () -> linkService.addLinkView("alias"));
    }

    @Test
    void shouldUpdateLink() {
        User user = new User();
        user.setId(1L);
        Link link = new Link("fullLink", "alias");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(linkDao.getUsersLinkByAlias("alias", 1L)).thenReturn(Optional.of(link));
        when(userDao.getByUsername(authentication.getName())).thenReturn(Optional.of(user));

        linkService.updateLink(link, "alias");

        verify(linkDao).update(any(Link.class));
    }

    @Test
    void shouldUpdateLinkByCreatingNewWhenAliasDifferent() {
        User user = new User();
        user.setId(1L);
        Link newLink = new Link("fullLink", "newAlias");
        Link link = new Link("fullLink", "alias");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(linkDao.getUsersLinkByAlias("alias", 1L)).thenReturn(Optional.of(link));
        when(userDao.getByUsername(authentication.getName())).thenReturn(Optional.of(user));

        linkService.updateLink(newLink, "alias");

        verify(linkDao, never()).update(any(Link.class));
        verify(linkDao).delete(any(Link.class));
        verify(linkDao).save(any(Link.class));
    }

    @Test
    void shouldNotUpdateLinkWhenNotFound() {
        User user = new User();
        user.setId(1L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(linkDao.getUsersLinkByAlias("alias", 1L)).thenReturn(Optional.empty());
        when(userDao.getByUsername(authentication.getName())).thenReturn(Optional.of(user));

        assertThrows(LinkNotFoundException.class, () -> linkService.updateLink(link, "alias"));
    }

    @Test
    void shouldNotUpdateLinkWhenAlreadyExist() {
        User user = new User();
        user.setId(1L);
        Link newLink = new Link("fullLink", "newAlias");
        Link link = new Link("fullLink", "alias");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(linkDao.getUsersLinkByAlias("alias", 1L)).thenReturn(Optional.of(link));
        when(linkDao.getUsersLinkByAlias("newAlias", 1L)).thenReturn(Optional.of(link));
        when(userDao.getByUsername(authentication.getName())).thenReturn(Optional.of(user));

        assertThrows(LinkAlreadyExistException.class, () -> linkService.updateLink(newLink, "alias"));
    }

    @Test
    void shouldRemoveLink() {
        User user = new User();
        user.setId(1L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userDao.getByUsername(any())).thenReturn(Optional.of(user));
        when(link.getUser()).thenReturn(user);
        when(linkDao.getUsersLinkByAlias("alias", 1L)).thenReturn(Optional.of(new Link()));

        linkService.removeLink("alias");

        verify(linkDao).delete(any(Link.class));
    }

    @Test
    void shouldNotRemoveLinkWhenNotFound() {
        User user = new User();
        user.setId(1L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userDao.getByUsername(any())).thenReturn(Optional.of(user));
        when(linkDao.getUsersLinkByAlias("alias", 1L)).thenReturn(Optional.empty());

        assertThrows(LinkNotFoundException.class, () -> linkService.removeLink("alias"));
    }

    @Test
    void shouldRemoveAllLinks() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(optionalUser.isPresent()).thenReturn(true);
        when(optionalUser.get()).thenReturn(user);
        when(userDao.getByUsername(authentication.getName())).thenReturn(optionalUser);
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