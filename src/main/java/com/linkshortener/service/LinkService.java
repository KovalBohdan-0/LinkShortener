package com.linkshortener.service;

import com.linkshortener.dao.LinkDao;
import com.linkshortener.dao.UserDao;
import com.linkshortener.entity.Link;
import com.linkshortener.entity.User;
import com.linkshortener.exception.LinkAlreadyExistException;
import com.linkshortener.exception.LinkNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * This class consist of methods that make business logic of creation,
 * retrieving, removing of links. Have possibility to add link to global
 * user with username anonymousUser to then get it by not authenticated
 * user. Authenticated user can do the same and also remove the link.
 *
 * @author Bohdan Koval
 * @see LinkDao
 * @see UserDao
 * @see Link
 * @see User
 */
@Service
@Transactional(readOnly = true)
public class LinkService {
    private final static Logger LOGGER = LoggerFactory.getLogger(LinkService.class);
    private final LinkDao linkDao;
    private final UserDao userDao;

    public LinkService(LinkDao linkDao, UserDao userDao) {
        this.linkDao = linkDao;
        this.userDao = userDao;
    }

    /**
     * Returns link by id if found, if not found in current user than returns
     * empty optional.
     *
     * @param id the id of link to return
     * @return optional of link, if not found - empty
     */
    public Optional<Link> getLinkById(long id) {
        Optional<Link> link = linkDao.get(id);
        Optional<User> user = getUserFromAuthContext();

        if (isUsersLink(user, link)) {
            return link;
        }

        return Optional.empty();
    }

    /**
     * Returns link by alias if found, if not found in current user than returns
     * empty optional.
     *
     * @param alias the id of link to return
     * @return optional of link, if not found - empty
     * @throws LinkNotFoundException if link with this alias was not found
     */
    public Link getUsersLinkByAlias(String alias) {
        Optional<Link> link = getUsersLink(alias);

        if (link.isPresent()) {
            return link.get();
        }

        throw new LinkNotFoundException(alias);
    }

    /**
     * Returns all links of current user. If user is not authenticated, returns
     * empty array.
     *
     * @return all link of current user
     */
    public List<Link> getAllLinks() {
        Optional<User> optionalUser = getUserFromAuthContext();
        List<Link> links = new ArrayList<>();

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Set<Link> foundLinks = user.getLinks();

            if (foundLinks != null && foundLinks.size() > 0) {
                links.addAll(user.getLinks());
            }
        }

        return links;
    }

    /**
     * If alias was found in database will redirect to link of current user.
     * If user not authenticated will try to find alias in anonymousUser.
     *
     * @param alias the alias of redirect link
     * @return redirect to saved link by alias or to not-found page
     */
    public ResponseEntity<HttpHeaders> redirectToUrl(String alias) {
        Optional<Link> link = getUsersLink(alias);
        HttpHeaders headers = new HttpHeaders();

        if (link.isEmpty()) {
            // Searches for link in global user with id 2
            link = linkDao.getUsersLinkByAlias(alias, 2);
        }

        if (link.isPresent()) {
            StringBuilder url = new StringBuilder(link.get().getFullLink());

            if (!url.toString().startsWith("http")) {
                url.insert(0, "https://");
            }

            headers.add("Location", url.toString());

            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }

        String frontendDomain = System.getenv("FRONTEND_DOMAIN") == null ? "localhost:4200" : System.getenv("FRONTEND_DOMAIN");
        headers.add("Location", "http://" + frontendDomain + "/app-not-found");

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    /**
     * Adds link, sets user to current user. If user not authenticated, link
     * adds to anonymousUser.
     *
     * @param link the link to add to current user
     * @throws LinkAlreadyExistException if link with this alias already exist
     */
    @Transactional
    public void addLink(Link link) {
        Optional<User> user = getUserFromAuthContext();

        if (user.isPresent() && getUsersLink(link.getAlias()).isEmpty()) {
            link.setUser(user.get());
            linkDao.save(link);
            return;
        }

        throw new LinkAlreadyExistException(link.getAlias());
    }

    /**
     * Adds link view.
     *
     * @param alias the alias of link to add view
     * @throws LinkNotFoundException if link with this alias not found
     */
    @Transactional
    public void addLinkView(String alias) {
        if (getUsersLink(alias).isPresent()) {
            Link link = getUsersLink(alias).get();
            link.setViews(link.getViews() + 1);
            return;
        }

        throw new LinkNotFoundException(alias);
    }

    /**
     * Updates link, sets fullLink to new. If given different alias, creates
     * new link and deletes previous.
     *
     * @param link the link to update to current user
     * @throws LinkAlreadyExistException if link with this alias already exist
     * @throws LinkNotFoundException     if link with this alias was not found
     */
    @Transactional
    public void updateLink(Link link, String alias) {
        Optional<User> user = getUserFromAuthContext();

        if (user.isPresent() && getUsersLink(alias).isPresent()) {
            if (alias.equals(link.getAlias())) {
                Link foundedLink = getUsersLink(alias).get();
                foundedLink.setFullLink(link.getFullLink());
                linkDao.update(foundedLink);
                return;
            } else {
                if (getUsersLink(link.getAlias()).isEmpty()) {
                    linkDao.delete(getUsersLink(alias).get());
                    link.setUser(user.get());
                    linkDao.save(link);
                    return;
                }

                throw new LinkAlreadyExistException(link.getAlias());
            }
        }

        throw new LinkNotFoundException(link.getAlias());
    }

    /**
     * Removes link by alias, if link found in current user. If user not
     * authenticated, nothing happens.
     *
     * @param alias the alias of link to remove
     * @throws LinkNotFoundException if link with this alias was not found
     */
    @Transactional
    public void removeLink(String alias) {
        Optional<Link> link = getUsersLink(alias);

        if (link.isPresent()) {
            linkDao.delete(link.get());
            return;
        }

        throw new LinkNotFoundException(alias);
    }

    /**
     * Removes all his links, if user is authenticated . If user not authenticated, nothing
     * happens.
     */
    @Transactional
    public void removeAllLinks() {
        Optional<User> user = getUserFromAuthContext();

        if (user.isPresent() && !user.get().getEmail().equals("anonymousUser")) {
            linkDao.deleteAllByUserId(user.get().getId());
        }
    }

    private Optional<User> getUserFromAuthContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            if (userDao.getByUsername(authentication.getName()).isPresent()) {
                return userDao.getByUsername(authentication.getName());
            }

            LOGGER.error("User with email: {} was not found", authentication.getName());
            throw new UsernameNotFoundException(String.format("User with email: %s was not found", authentication.getName()));
        }

        return Optional.empty();
    }

    private boolean isUsersLink(Optional<User> user, Optional<Link> link) {
        return link.isPresent()
                && user.isPresent()
                && Objects.equals(link.get().getUser().getId(), user.get().getId());
    }

    private Optional<Link> getUsersLink(String alias) {
        Optional<User> user = getUserFromAuthContext();

        if (user.isPresent() && linkDao.getUsersLinkByAlias(alias, user.get().getId()).isPresent()) {
            return linkDao.getUsersLinkByAlias(alias, user.get().getId());
        }

        return Optional.empty();
    }
}
