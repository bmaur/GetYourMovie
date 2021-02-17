package com.finalproject.filmweb.movie.entity;

import com.finalproject.filmweb.user.UserEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "movies_to_watch")
public class MovieToWatchEntity {


    @EmbeddedId
    private final MovieToWatchKey id = new MovieToWatchKey();

    @ManyToOne
    @MapsId("movieID")
    @JoinColumn(name = "movie_id")
    private MovieSearchEntity movie;


    @ManyToOne
    @MapsId("userID")
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Override
    public String toString() {
        return "MovieToWatchEntity{" +
                "id=" + id +
                ", movie=" + movie +
                ", user=" + user +
                '}';
    }

}
