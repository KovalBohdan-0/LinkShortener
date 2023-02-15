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

    public Optional<Link> getLinkById(long id) {
        Optional<Link> optionalLink = linkDao.get(id);
        Optional<User> optionalUser = getUserFromAuthContext();

        if (isUsersLink(optionalUser, optionalLink)) {
            return optionalLink;
        }

        return Optional.empty();
    }

    public Optional<Link> getLinkByAlias(String alias) {
        Optional<Link> optionalLink = linkDao.getLinkByAlias(alias);
        Optional<User> optionalUser = getUserFromAuthContext();

        if (isUsersLink(optionalUser, optionalLink)) {
            return optionalLink;
        }

        return Optional.empty();
    }

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

    @Transactional
    public void addLink(Link link) {
        Optional<User> optionalUser = getUserFromAuthContext();

        if (optionalUser.isPresent()) {
            link.setUser(optionalUser.get());
            linkDao.save(link);
            LOGGER.info("Saved link :{} to user with username :{}", link, link.getUser().getEmail());
        }
    }

    @Transactional
    public void removeLink(long id) {
        Optional<User> optionalUser = getUserFromAuthContext();
        Optional<Link> optionalLink = getLinkById(id);

        if (isUsersLink(optionalUser, optionalLink)) {
            linkDao.get(id).ifPresent(linkDao::delete);
        }
    }

    @Transactional
    public void removeAllLinks() {
        Optional<User> optionalUser = getUserFromAuthContext();

        if (optionalUser.isPresent()) {
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
        if (link.isPresent()
                && user.isPresent()
                && Objects.equals(link.get().getUser().getId(), user.get().getId())) {
            return true;
        }

        return false;
    }
}
