package com.finalproject.filmweb.user.exception;

public final class WrongUserPasswordException extends RuntimeException {

    public WrongUserPasswordException() {
        super("Invalid user password!");
    }
}
