package com.finalproject.filmweb.movie.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
@Getter
@Setter
@Entity
@Table(name = "movies")
@NoArgsConstructor
public class MovieSearchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String title;

    private String year;

    private String imdbID;

    private String type;

    private String poster;


    @Override
    public String toString() {
        return "MovieSearchEntity{" +
                "Id=" + Id +
                ", title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", imdbID='" + imdbID + '\'' +
                ", type='" + type + '\'' +
                ", poster='" + poster + '\'' +
                '}';
    }

}
