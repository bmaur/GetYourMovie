package com.finalproject.filmweb.movie.exception;

public class MovieAlreadyExistInToWatchException extends RuntimeException {
    public MovieAlreadyExistInToWatchException() {
        super("The movie already exists in to watch section");
    }
}
