package com.linkshortener.controller;

import com.linkshortener.dto.LinkDto;
import com.linkshortener.entity.Link;
import com.linkshortener.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
@CrossOrigin
@RequestMapping("/api/links")
public class LinkController {
    private final LinkService linkService;
    private final Logger LOGGER = LoggerFactory.getLogger(LinkController.class);

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @Operation(summary = "Returns link by alias",
            description = "Returns link by alias. If alias was found in database will return the link of current user. If user not authenticated will try to find alias in anonymousUser.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully found redirect link",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LinkDto.class))}),
            @ApiResponse(responseCode = "404", description = "alias was not found")})
    @GetMapping("/{alias}")
    public ResponseEntity<LinkDto> getLinkByAlias(@PathVariable String alias) {
        Optional<Link> link = linkService.getUsersLinkByAlias(alias);

        if (link.isPresent()) {
            return new ResponseEntity<>(convertToLinkDto(link.get()), HttpStatus.OK);
        }

        LOGGER.warn("Searched alias :{} of link does not exist", alias);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Returns all links",
            description = "Return current user links. Nothing will return if user not authenticated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "returned all found links",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LinkDto.class))}),
            @ApiResponse(responseCode = "403", description = "user not found")})
    @GetMapping
    public List<LinkDto> getLinks() {
        return linkService.getAllLinks().stream().map(this::convertToLinkDto).collect(Collectors.toList());
    }

    @Operation(summary = "Adds link",
            description = "Adds link to current user. If user not authenticated will add link to anonymousUser. Fails if alias is already used.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "link added"),
            @ApiResponse(responseCode = "400", description = "not valid link"),
            @ApiResponse(responseCode = "409", description = "alias already exist")})
    @PostMapping
    public ResponseEntity<Void> addLink(@Valid @RequestBody LinkDto linkDto) {
        Link link = convertToLink(linkDto);

        if (linkService.getUsersLinkByAlias(link.getAlias()).isEmpty()) {
            linkService.addLink(link);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        LOGGER.warn("Link with alias :{} already exist", linkDto.getAlias());
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @Operation(summary = "Updates link",
            description = "Updates link of current user. Fails if users link not found by alias. If user wants to update alias, deletes previous link and creates new.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "link added"),
            @ApiResponse(responseCode = "400", description = "not valid link"),
            @ApiResponse(responseCode = "409", description = "alias already exist")})
    @PutMapping("/{alias}")
    public ResponseEntity<Void> updateLink(@Valid @RequestBody LinkDto linkDto, @PathVariable String alias) {
        Link link = convertToLink(linkDto);
        Optional<Link> existingLink = linkService.getUsersLinkByAlias(alias);

        if (existingLink.isPresent()) {
            if (link.getAlias().equals(alias)) {
                linkService.updateLink(link);
            } else {
                linkService.removeLink(existingLink.get().getId());
                //TODO
                linkService.addLink(link);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        }

        LOGGER.warn("Link with alias :{} was not found", linkDto.getAlias());
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @Operation(summary = "Removes link",
            description = "Removes link from current user. Not removes links from anonymousUser.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "removed link from user"),
            @ApiResponse(responseCode = "404", description = "link with this id was not found(for this user)")})
    @DeleteMapping("/{alias}")
    public ResponseEntity<Void> removeLinkByAlias(@PathVariable String alias) {
        Optional<Link> link = linkService.getUsersLinkByAlias(alias);

        if (link.isPresent()) {
            linkService.removeLink(link.get().getId());

            return new ResponseEntity<>(HttpStatus.OK);
        }

        LOGGER.warn("Searched link with id :{} does not exist", alias);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Removes all links",
            description = "Removes all links from current user. Not removes links from anonymousUser.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "removed all user links"),
            @ApiResponse(responseCode = "404", description = "not found user")})
    @DeleteMapping
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
