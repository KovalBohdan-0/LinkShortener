package com.linkshortener.service;

import com.linkshortener.dao.LinkDao;
import com.linkshortener.dao.UserDao;
import com.linkshortener.entity.Link;
import com.linkshortener.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
     * empty optional. If username not found throw UsernameNotFoundException.
     * @param id the id of link to return
     * @return optional of link, if not found - empty
     */
    public Optional<Link> getLinkById(long id) {
        Optional<Link> optionalLink = linkDao.get(id);
        Optional<User> optionalUser = getUserFromAuthContext();

        if (isUsersLink(optionalUser, optionalLink)) {
            return optionalLink;
        }

        return Optional.empty();
    }

    /**
     * Returns link by alias if found, if not found in current user than returns
     * empty optional. If username not found throw UsernameNotFoundException.
     * @param alias the id of link to return
     * @return optional of link, if not found - empty
     */
    public Optional<Link> getLinkByAlias(String alias) {
        Optional<Link> optionalLink = linkDao.getLinkByAlias(alias);
        Optional<User> optionalUser = getUserFromAuthContext();

        if (isUsersLink(optionalUser, optionalLink)) {
            return optionalLink;
        }

        return Optional.empty();
    }

    /**
     * Returns all links of current user. If user is not authenticated, nothing
     * happens. If username not found throw UsernameNotFoundException.
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
     * Adds link, sets user to current user. If user not authenticated, link
     * adds to anonymousUser. If username not found throw UsernameNotFoundException.
     * @param link the link to add to current user
     */
    @Transactional
    public void addLink(Link link) {
        Optional<User> optionalUser = getUserFromAuthContext();

        if (optionalUser.isPresent()) {
            link.setUser(optionalUser.get());
            linkDao.save(link);
            LOGGER.info("Saved link :{} to user with username :{}", link, link.getUser().getEmail());
        }
    }

    /**
     * Removes link by id, if link found in current user. If user not
     * authenticated, nothing happens. If username not found throw UsernameNotFoundException.
     * @param id the id of link to remove
     */
    @Transactional
    public void removeLink(long id) {
        Optional<User> optionalUser = getUserFromAuthContext();
        Optional<Link> optionalLink = getLinkById(id);

        if (isUsersLink(optionalUser, optionalLink)) {
            linkDao.get(id).ifPresent(linkDao::delete);
        }
    }

    /**
     * Removes all his links, if user is authenticated . If user not authenticated, nothing
     * happens. If username not found throw UsernameNotFoundException.
     */
    @Transactional
    public void removeAllLinks() {
        Optional<User> optionalUser = getUserFromAuthContext();

        if (optionalUser.isPresent() && !optionalUser.get().getEmail().equals("anonymousUser")) {
            linkDao.deleteAllByUserId(optionalUser.get().getId());
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
}
