package com.example.linkshortener.service;

import com.example.linkshortener.dao.LinkDao;
import com.example.linkshortener.entity.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LinkService {
    private final static Logger LOGGER = LoggerFactory.getLogger(LinkService.class);
    private final LinkDao linkDao;

    public LinkService(LinkDao linkDao) {
        this.linkDao = linkDao;
    }

    public List<Link> getAllLinks() {
        return linkDao.getAll();
    }

    public void removeAllLinks() {
        getAllLinks().forEach(link -> removeLink(link.getId()));
    }

    public void addLink(Link link) {
        LOGGER.info(link.toString());
        linkDao.save(link);
    }

    public void removeLink(Long id) {
        linkDao.get(id).ifPresent(linkDao::delete);
    }
}
