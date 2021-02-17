package com.finalproject.filmweb.movie.repository;

import com.finalproject.filmweb.movie.entity.MovieToWatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieToWatchRepository extends JpaRepository<MovieToWatchEntity, Long> {
    @Query(value = "SELECT f.movie_id,f.user_id FROM movies_to_watch f JOIN users u ON f.user_id =u.id WHERE u.id=:userId ",
            nativeQuery = true)
    List<MovieToWatchEntity> findByUserId(Long userId);
}
