package com.linkshortener.controller;

import com.linkshortener.entity.Link;
import com.linkshortener.service.LinkService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Validated
@RestController
public class LinkController {
    LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping("{alias}")
    public RedirectView redirectToUrl(@PathVariable String alias) {
        RedirectView redirectView = new RedirectView();
        Link link = linkService.getLinkByShortLink(alias).orElseThrow();
        //TODO add not found
        redirectView.setUrl("https://www." + link.getFullLink());

        return redirectView;
    }

    @GetMapping("/api/links")
    public List<Link> getLinks() {
        return linkService.getAllLinks();
    }

    //TODO add dto class
    @PostMapping("/api/links")
    public void addLink(@Valid @RequestBody Link link) {
        linkService.addLink(link);
    }

    @DeleteMapping("/api/links/{id}")
    public void removeLink(@PathVariable Long id) {
        linkService.removeLink(id);
    }

    @DeleteMapping("/api/links")
    public void removeLinks() {
        linkService.removeAllLinks();
    }
}
