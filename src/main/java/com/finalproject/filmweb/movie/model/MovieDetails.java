package com.finalproject.filmweb.movie.model;

import com.finalproject.filmweb.movie.comment.Comment;
import lombok.Builder;
import lombok.Value;

import java.util.List;
@Value
@Builder
public class MovieDetails {

     String title;
     String year;
     String rated;
     String released;
     String genre;
     String director;
     String writer;
     String actors;
     String plot;
     String language;
     String country;
     String awards;
     String poster_url;
     List<Ratings> ratings;
     String type;
     String dvd;
     String boxOffice;
     String production;
     String imdbId;
     boolean isMyFavoritesMovies;
     boolean isMyToWatchMovies;
     List<Comment> comments;

}
