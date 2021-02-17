package com.finalproject.filmweb.user.exception;

public class UserNickNotFoundException extends RuntimeException {
    public UserNickNotFoundException() {
        super("Invalid User Nick!");
    }
}
