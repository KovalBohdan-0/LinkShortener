package com.linkshortener.service;

import com.linkshortener.dao.LinkDao;
import com.linkshortener.dao.UserDao;
import com.linkshortener.entity.Link;
import com.linkshortener.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public List<Link> getAllLinks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Link> links = new ArrayList<>();

        if (authentication != null) {
            User user = userDao.getByUsername(authentication.getName());
            Set<Link> foundLinks = user.getLinks();

            if (foundLinks != null && foundLinks.size() > 0) {
                links.addAll(user.getLinks());
            }

            LOGGER.info("All user links :" + links + " For user :" + user.getEmail());
        }

        return links;
    }

    @Transactional
    public boolean addLink(Link link) {
        //TODO add links for anonymous users
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        link.setUser(userDao.getByUsername(authentication.getName()));
        linkDao.save(link);
        LOGGER.info("Saved link :" + link);

        return true;
    }

    @Transactional
    public void removeLink(Long id) {
        linkDao.get(id).ifPresent(linkDao::delete);
    }

    @Transactional
    public void removeAllLinks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            User user = userDao.getByUsername(authentication.getName());
            linkDao.deleteAllByUserId(user.getId());
        }
    }
}
