package com.finalproject.filmweb.user.exception;

public class UserNickAlreadyExistException extends RuntimeException {
    public UserNickAlreadyExistException(String userNick) {
        super("There is an account with that nick name : " + userNick);
    }
}
