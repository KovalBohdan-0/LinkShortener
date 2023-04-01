package com.linkshortener.service;

import com.linkshortener.repository.LinkRepository;
import com.linkshortener.repository.UserRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class LinkServiceTest {
    @InjectMocks
    private LinkService linkService;
    @Mock
    private LinkRepository linkRepository;
    @Mock
    private UserRepository userRepository;
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
        linkService = new LinkService(linkRepository, userRepository);
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
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(link.getUser()).thenReturn(user);
        when(linkRepository.findById(anyLong())).thenReturn(Optional.of(link));

        boolean founded = linkService.getLinkById(anyLong()).isPresent();

        verify(linkRepository).findById(anyLong());
        assertThat(founded).isTrue();
    }

    @Test
    void shouldNotGetLinkByIdWhenNotFound() {
        User user = new User();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(link.getUser()).thenReturn(user);
        when(linkRepository.findById(anyLong())).thenReturn(Optional.empty());

        boolean founded = linkService.getLinkById(anyLong()).isPresent();

        verify(linkRepository).findById(anyLong());
        assertThat(founded).isFalse();
    }

    @Test
    void shouldfindLinkByUserIdAndAlias() {
        User user = new User();
        user.setId(1L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(link.getUser()).thenReturn(user);
        when(linkRepository.findLinkByUserIdAndAlias(1L, "alias")).thenReturn(Optional.of(link));

        boolean founded = linkService.getUsersLinkByAlias("alias") != null;

        verify(linkRepository, atLeastOnce()).findLinkByUserIdAndAlias(1L, "alias");
        assertThat(founded).isTrue();
    }

    @Test
    void shouldThrowNotFoundWhenGettingLinkByNotFoundAlias() {
        User user = new User();
        user.setId(1L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(linkRepository.findLinkByUserIdAndAlias(1L, "alias")).thenReturn(Optional.empty());

        assertThrows(LinkNotFoundException.class, () -> linkService.getUsersLinkByAlias("alias"));
    }

    @Test
    void shouldGetAllLinks() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(optionalUser.isPresent()).thenReturn(true);
        when(userRepository.findByEmail(authentication.getName())).thenReturn(optionalUser);
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
        when(userRepository.findByEmail(authentication.getName())).thenReturn(Optional.of(user));
        when(link.getUser()).thenReturn(user);

        linkService.addLink(link);

        verify(linkRepository).save(any(Link.class));
    }

    @Test
    void shouldNotAddLinkWhenAlreadyExist() {
        User user = new User();
        user.setId(1L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(optionalUser.isPresent()).thenReturn(true);
        when(linkRepository.findLinkByUserIdAndAlias(1L, "alias")).thenReturn(Optional.of(link));
        when(userRepository.findByEmail(authentication.getName())).thenReturn(Optional.of(user));
        when(link.getUser()).thenReturn(user);


        assertThrows(LinkAlreadyExistException.class, () -> linkService.addLink(new Link("fullLink", "alias")));
    }

    @Test
    void shouldAddLinkView() {
        User user = new User();
        user.setId(1L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(linkRepository.findLinkByUserIdAndAlias(1L, "alias")).thenReturn(Optional.of(link));
        when(linkRepository.findLinksByAlias("alias")).thenReturn(List.of(link));
        when(userRepository.findByEmail(authentication.getName())).thenReturn(Optional.of(user));

        linkService.addLinkView("alias");

        verify(link).setViews(anyLong());
    }

    @Test
    void shouldNotAddLinkViewWhenNotFound() {
        User user = new User();
        user.setId(1L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(linkRepository.findLinkByUserIdAndAlias(1L, "alias")).thenReturn(Optional.empty());
        when(userRepository.findByEmail(authentication.getName())).thenReturn(Optional.of(user));

        assertThrows(LinkNotFoundException.class, () -> linkService.addLinkView("alias"));
    }

    @Test
    void shouldUpdateLink() {
        User user = new User();
        user.setId(1L);
        Link link = new Link("fullLink", "alias");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(linkRepository.findLinkByUserIdAndAlias(1L, "alias")).thenReturn(Optional.of(link));
        when(userRepository.findByEmail(authentication.getName())).thenReturn(Optional.of(user));

        linkService.updateLink(link, "alias");

        verify(linkRepository).update(link.getId(), link.getAlias(), link.getFullLink(), link.getViews());
    }

    @Test
    void shouldUpdateLinkByCreatingNewWhenAliasDifferent() {
        User user = new User();
        user.setId(1L);
        Link newLink = new Link("fullLink", "newAlias");
        Link link = new Link("fullLink", "alias");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(linkRepository.findLinkByUserIdAndAlias(1L, "alias")).thenReturn(Optional.of(link));
        when(userRepository.findByEmail(authentication.getName())).thenReturn(Optional.of(user));

        linkService.updateLink(newLink, "alias");

        verify(linkRepository, never()).update(link.getId(), link.getAlias(), link.getFullLink(), link.getViews());
        verify(linkRepository).delete(any(Link.class));
        verify(linkRepository).save(any(Link.class));
    }

    @Test
    void shouldNotUpdateLinkWhenNotFound() {
        User user = new User();
        user.setId(1L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(linkRepository.findLinkByUserIdAndAlias(1L, "alias")).thenReturn(Optional.empty());
        when(userRepository.findByEmail(authentication.getName())).thenReturn(Optional.of(user));

        assertThrows(LinkNotFoundException.class, () -> linkService.updateLink(link, "alias"));
    }

    @Test
    void shouldNotUpdateLinkWhenAlreadyExist() {
        User user = new User();
        user.setId(1L);
        Link newLink = new Link("fullLink", "newAlias");
        Link link = new Link("fullLink", "alias");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(linkRepository.findLinkByUserIdAndAlias(1L, "alias")).thenReturn(Optional.of(link));
        when(linkRepository.findLinkByUserIdAndAlias(1L, "newAlias")).thenReturn(Optional.of(link));
        when(userRepository.findByEmail(authentication.getName())).thenReturn(Optional.of(user));

        assertThrows(LinkAlreadyExistException.class, () -> linkService.updateLink(newLink, "alias"));
    }

    @Test
    void shouldRemoveLink() {
        User user = new User();
        user.setId(1L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(link.getUser()).thenReturn(user);
        when(linkRepository.findLinkByUserIdAndAlias(1L, "alias")).thenReturn(Optional.of(new Link()));

        linkService.removeLink("alias");

        verify(linkRepository).delete(any(Link.class));
    }

    @Test
    void shouldNotRemoveLinkWhenNotFound() {
        User user = new User();
        user.setId(1L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(linkRepository.findLinkByUserIdAndAlias(1L, "alias")).thenReturn(Optional.empty());

        assertThrows(LinkNotFoundException.class, () -> linkService.removeLink("alias"));
    }

    @Test
    void shouldRemoveAllLinks() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(optionalUser.isPresent()).thenReturn(true);
        when(optionalUser.get()).thenReturn(user);
        when(userRepository.findByEmail(authentication.getName())).thenReturn(optionalUser);
        when(user.getEmail()).thenReturn("email");

        linkService.removeAllLinks();

        verify(linkRepository).deleteAllByUserId(anyLong());
    }

    @Test
    void shouldNotRemoveAllLinksWhenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);

        linkService.removeAllLinks();

        verify(linkRepository, never()).deleteAll();
    }
}