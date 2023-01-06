package com.example.linkshortener.service;

import com.example.linkshortener.entity.Link;
import com.example.linkshortener.repository.LinkRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LinkService {
    private final LinkRepository linkRepository;

    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public List<Link> getAllLinks() {
        return linkRepository.findAll();
    }

    public void removeAllLinks() {
        linkRepository.deleteAll();
    }

    public void addLink(Link link) {
        linkRepository.save(link);
    }

    public void removeLink(Long id) {
        linkRepository.deleteById(id);
    }
}
