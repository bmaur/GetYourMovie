package com.finalproject.filmweb.user.exception;

public final class UserAlreadyExistException extends RuntimeException {

    public UserAlreadyExistException(String userName) {
        super("There is an account with that email address: " + userName);
    }
}
