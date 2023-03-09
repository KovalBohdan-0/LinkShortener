package com.linkshortener.exception;

public class LinkNotFoundException extends RuntimeException{
    public LinkNotFoundException(String alias) {
        super(String.format("Link with alias %s was not found", alias));
    }
}
