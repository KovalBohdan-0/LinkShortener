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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public LinkDto getLinkByAlias(@PathVariable String alias) {
            return convertToLinkDto(linkService.getUsersLinkByAlias(alias));
    }

    @Operation(summary = "Returns all links",
            description = "Return current user links. Nothing will return if user not authenticated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "returned all found links",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LinkDto.class))}),
            @ApiResponse(responseCode = "403", description = "forbidden to anonymousUser")})
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
    public void addLink(@Valid @RequestBody LinkDto linkDto) {
        Link link = convertToLink(linkDto);
        linkService.addLink(link);
    }

    @Operation(summary = "Adds link view",
            description = "Adds one to links views counter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "added to links views counter"),
            @ApiResponse(responseCode = "404", description = "link with this alias was not found")})
    @PutMapping("/{alias}/view")
    public void addLinkView(@PathVariable String alias) {
        linkService.addLinkView(alias);
    }

    @Operation(summary = "Updates link",
            description = "Updates link from current user, if alias is the same updates link, if different, creates new")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "updated link for current user"),
            @ApiResponse(responseCode = "400", description = "not valid link"),
            @ApiResponse(responseCode = "409", description = "alias already exist")})
    @PutMapping("/{alias}")
    public void updateLink(@Valid @RequestBody LinkDto linkDto, @PathVariable String alias) {
        Link link = convertToLink(linkDto);
        linkService.updateLink(link, alias);
    }

    @Operation(summary = "Removes link",
            description = "Removes link from current user. Not removes links from anonymousUser.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "removed link from user"),
            @ApiResponse(responseCode = "404", description = "link with this alias was not found(for this user)")})
    @DeleteMapping("/{alias}")
    public void removeLinkByAlias(@PathVariable String alias) {
        linkService.removeLink(alias);
    }

    @Operation(summary = "Removes all links",
            description = "Removes all links from current user. Not removes links from anonymousUser.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "removed all user links"),
            @ApiResponse(responseCode = "403", description = "forbidden to anonymousUser")})
    @DeleteMapping
    public void removeLinks() {
        linkService.removeAllLinks();
    }

    private LinkDto convertToLinkDto(Link link) {
        return new LinkDto(link.getFullLink(), link.getAlias(), link.getViews(), link.getDate());
    }

    private Link convertToLink(LinkDto linkDto) {
        return new Link(linkDto.getFullLink(), linkDto.getAlias());
    }
}
