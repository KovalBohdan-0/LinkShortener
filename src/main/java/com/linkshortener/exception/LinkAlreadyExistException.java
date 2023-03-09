package com.linkshortener.exception;

public class LinkAlreadyExistException extends RuntimeException {
    public LinkAlreadyExistException(String alias) {
        super(String.format("Link with this alias %s already exist", alias));
    }
}
