package com.finalproject.filmweb.movie.repository;

import com.finalproject.filmweb.movie.entity.MovieSearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieRepository extends JpaRepository<MovieSearchEntity, Long> {
    Optional<MovieSearchEntity> findByTitle(String title);

    Optional<MovieSearchEntity> findByImdbID(String imdbID);

    Optional<MovieSearchEntity> findById(Long imdbID);

}
