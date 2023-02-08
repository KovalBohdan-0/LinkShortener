package com.linkshortener.controller;

import com.linkshortener.entity.Link;
import com.linkshortener.service.LinkService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api")
public class LinkController {
    LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping("/links")
    public List<Link> getLinks() {
        return linkService.getAllLinks();
    }

    @DeleteMapping("/links")
    public void removeLinks() {
        linkService.removeAllLinks();
    }

    //TODO add dto class
    @PostMapping("/links")
    public void addLink(@Valid @RequestBody Link link) {
        linkService.addLink(link);
    }

    @DeleteMapping("/links/{id}")
    public void removeLink(@PathVariable Long id) {
        linkService.removeLink(id);
    }

}
