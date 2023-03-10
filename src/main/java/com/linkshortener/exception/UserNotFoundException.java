package com.linkshortener.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super(String.format("User with email %s and password not found", email));
    }
}
