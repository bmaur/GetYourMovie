package com.finalproject.filmweb.movie.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
public class MovieToWatchKey implements Serializable {


    @Column(name = "movie_id")
    private Long movieID;

    @Column(name = "user_id")
    private Long userID;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieToWatchKey that = (MovieToWatchKey) o;
        return Objects.equals(movieID, that.movieID) &&
                Objects.equals(userID, that.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieID, userID);
    }
}
