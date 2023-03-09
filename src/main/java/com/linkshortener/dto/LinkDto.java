package com.linkshortener.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class LinkDto {
    @NotBlank(message = "Full link is mandatory")
    private String fullLink;
    @NotBlank(message = "Alias is mandatory")
    private String alias;
    private Long views;
    private LocalDateTime date;

    public LinkDto() {
    }

    public LinkDto(String fullLink, String alias) {
        this.fullLink = fullLink;
        this.alias = alias;
    }

    public LinkDto(String fullLink, String alias, Long views, LocalDateTime date) {
        this.fullLink = fullLink;
        this.alias = alias;
        this.views = views;
        this.date = date;
    }

    public String getFullLink() {
        return fullLink;
    }

    public void setFullLink(String fullLink) {
        this.fullLink = fullLink;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
