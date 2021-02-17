package com.finalproject.filmweb.movie.comment;

import com.finalproject.filmweb.movie.entity.MovieSearchEntity;
import com.finalproject.filmweb.user.UserEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "comments")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private LocalDate created;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private MovieSearchEntity movie;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Override
    public String toString() {
        return "CommentEntity{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", movie=" + movie +
                ", user=" + user +
                '}';
    }

}
