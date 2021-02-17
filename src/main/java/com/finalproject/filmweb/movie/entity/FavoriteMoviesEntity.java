package com.finalproject.filmweb.movie.entity;

import com.finalproject.filmweb.user.UserEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
@Getter
@Setter
@Entity
@Table(name = "favorites")
public class FavoriteMoviesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private MovieSearchEntity movie;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Override
    public String toString() {
        return "FavoriteMoviesEntity{" +
                "id=" + id +
                ", movie=" + movie +
                ", user=" + user +
                '}';
    }

}
