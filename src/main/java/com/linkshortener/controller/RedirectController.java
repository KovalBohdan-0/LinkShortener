package com.linkshortener.controller;

import com.linkshortener.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class RedirectController {
    private final LinkService linkService;

    public RedirectController(LinkService linkService) {
        this.linkService = linkService;
    }

    @Operation(summary = "Redirects to url by alias",
            description = "If alias was found in database will redirect to full link of current user. If user not authenticated will try to find alias in anonymousUser.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully redirect to url")})
    @GetMapping("/l/{alias}")
    public ResponseEntity<HttpHeaders> redirectToUrl(@PathVariable String alias) {
        return linkService.redirectToUrl(alias);
    }
}
