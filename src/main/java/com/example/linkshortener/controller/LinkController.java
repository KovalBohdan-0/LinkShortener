package com.example.linkshortener.controller;

import com.example.linkshortener.entity.Link;
import com.example.linkshortener.service.LinkService;
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

    @PostMapping("/links")
    public void addLink(@Valid @RequestBody Link link) {
        linkService.addLink(link);
    }

    @DeleteMapping("/links/{id}")
    public void removeLink(@PathVariable Long id) {
        linkService.removeLink(id);
    }

}
