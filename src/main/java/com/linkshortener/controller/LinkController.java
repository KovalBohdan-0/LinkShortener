package com.linkshortener.controller;

import com.linkshortener.dto.LinkDto;
import com.linkshortener.entity.Link;
import com.linkshortener.service.LinkService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Validated
@RestController
public class LinkController {
    private final LinkService linkService;
    private final Logger LOGGER = LoggerFactory.getLogger(LinkController.class);

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping("/{alias}")
    public ResponseEntity<HttpHeaders> redirectToUrl(@PathVariable String alias) {
        Optional<Link> link = linkService.getLinkByAlias(alias);

        if (link.isPresent()) {
            HttpHeaders headers = new HttpHeaders();
            StringBuilder url = new StringBuilder(link.get().getFullLink());

            if (!url.toString().startsWith("http")) {
                url.insert(0, "https://");
            }

            headers.add("Location", url.toString());

            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }

        LOGGER.warn("Searched alias :{} of link does not exist", alias);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/api/links")
    public List<LinkDto> getLinks() {
        return linkService.getAllLinks().stream().map(this::convertToLinkDto).collect(Collectors.toList());
    }

    @PostMapping("/api/links")
    public ResponseEntity<Void> addLink(@Valid @RequestBody LinkDto linkDto) {
        Link link = convertToLink(linkDto);

        if (linkService.getLinkByAlias(link.getAlias()).isEmpty()) {
            linkService.addLink(link);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        LOGGER.warn("Link with alias :{} already exist", linkDto.getAlias());
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @DeleteMapping("/api/links/{id}")
    public ResponseEntity<Void> removeLink(@PathVariable Long id) {
        Optional<Link> link = linkService.getLinkById(id);

        if (link.isPresent()) {
            linkService.removeLink(id);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        LOGGER.warn("Searched link with id :{} does not exist", id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/api/links")
    public void removeLinks() {
        linkService.removeAllLinks();
    }

    private LinkDto convertToLinkDto(Link link) {
        return new LinkDto(link.getFullLink(), link.getAlias());
    }

    private Link convertToLink(LinkDto linkDto) {
        return new Link(linkDto.getFullLink(), linkDto.getAlias());
    }
}
