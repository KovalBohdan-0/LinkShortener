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
import org.springframework.http.HttpHeaders;
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
public class LinkController {
    private final LinkService linkService;
    private final Logger LOGGER = LoggerFactory.getLogger(LinkController.class);

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @Operation(summary = "If alias was found in database will redirect to full link of current user." +
            " If user not authenticated will try to find alias in anonymousUser.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "successfully redirected",
                    content = {@Content(mediaType = "application/json",
                            schema =  @Schema(implementation = HttpHeaders.class))}),
            @ApiResponse(responseCode = "404", description = "alias was not found")
    })
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

    @Operation(summary = "Return current user links. Nothing will return if user not authenticated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "returned all found links",
                    content = {@Content(mediaType = "application/json",
                            schema =  @Schema(implementation = LinkDto.class))}),
            @ApiResponse(responseCode = "403", description = "user not found")
    })
    @GetMapping("/api/links")
    public List<LinkDto> getLinks() {
        return linkService.getAllLinks().stream().map(this::convertToLinkDto).collect(Collectors.toList());
    }

    @Operation(summary = "Adds link to current user. If user not authenticated will add link to anonymousUser. Fails if alias is already used.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "link added"),
            @ApiResponse(responseCode = "400", description = "not valid link"),
            @ApiResponse(responseCode = "409", description = "alias already exist")
    })
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

    @Operation(summary = "Removes link from current user. Not removes links from anonymousUser.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "removed link from user"),
            @ApiResponse(responseCode = "404", description = "link with this id was not found(for this user)")
    })
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

    @Operation(summary = "Removes all links from current user. Not removes links from anonymousUser.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "removed all user links"),
            @ApiResponse(responseCode = "404", description = "not found user")
    })
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
