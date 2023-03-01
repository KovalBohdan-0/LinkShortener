package com.linkshortener.dto;

import jakarta.validation.constraints.NotBlank;

public class LinkDto {
    @NotBlank(message = "Full link is mandatory")
    private String fullLink;
    @NotBlank(message = "Alias is mandatory")
    private String alias;

    public LinkDto() {
    }

    public LinkDto(String fullLink, String alias) {
        this.fullLink = fullLink;
        this.alias = alias;
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
}
