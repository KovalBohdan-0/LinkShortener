package com.linkshortener.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("User with this email and password was not found");
    }
}
