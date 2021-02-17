package com.finalproject.filmweb.user.exception;

public final class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Invalid username!");
    }
}
