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

/**
 * This class consist of secured api endpoints that create short links of some
 * urls. Also gives abilities to add link to user, get and delete it.
 *
 * @author Bohdan Koval
 * @see LinkService
 * @see Link
 * @see LinkDto
 */
@Validated
@RestController
public class LinkController {
    private final LinkService linkService;
    private final Logger LOGGER = LoggerFactory.getLogger(LinkController.class);

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    /**
     * If alias was found in database will redirect to full link of current user.
     * If user not authenticated will try to find alias in anonymousUser.
     *
     * @param alias the alias of redirect link
     * @return redirect to saved link by alias, HTTP status code
     * 302 - successfully redirected,
     * 404 - alias was not found
     */
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

    /**
     * Return current user links. Nothing will return if user not authenticated.
     *
     * @return all current user links, HTTP status code
     * 200 - returned all found links
     * 404 - user not found
     */
    @GetMapping("/api/links")
    public List<LinkDto> getLinks() {
        return linkService.getAllLinks().stream().map(this::convertToLinkDto).collect(Collectors.toList());
    }

    /**
     * Adds link to current user. If user not authenticated will add link to anonymousUser.
     * Fails if alias is already used.
     * Usage:
     * <pre>
     * {
     *   "fullLink": "www.youtube.com",
     *   "alias": "youtube"
     * }
     * </pre>
     *
     * @param linkDto link to add
     * @return HTTP status code
     * 200 - link added,
     * 400 - not valid link,
     * 409 - alias already exist
     */
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

    /**
     * Removes current user link by id, will not remove link of other user.
     *
     * @param id the id of link to delete
     * @return HTTP status code
     * 200 - removed link,
     * 404 - link with this id was not found(for this user)
     */
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

    /**
     * Removes all links from current user. Not removes links from anonymousUser.
     * Returns HTTP status code
     * 200 - removed all user link,
     * 404 - not found user
     */
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
