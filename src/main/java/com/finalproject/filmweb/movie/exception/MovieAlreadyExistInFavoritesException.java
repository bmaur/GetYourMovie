package com.finalproject.filmweb.movie.exception;

public class MovieAlreadyExistInFavoritesException extends RuntimeException {
    public MovieAlreadyExistInFavoritesException() {
        super("The movie already exists in favorites");
    }
}
