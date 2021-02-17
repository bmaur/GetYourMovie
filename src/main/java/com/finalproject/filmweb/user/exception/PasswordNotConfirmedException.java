package com.finalproject.filmweb.user.exception;

public class PasswordNotConfirmedException extends RuntimeException {

    public PasswordNotConfirmedException() {
        super("Your password and confirmation password do not match.");
    }
}
