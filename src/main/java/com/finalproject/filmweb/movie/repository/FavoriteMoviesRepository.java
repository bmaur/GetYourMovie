package com.finalproject.filmweb.movie.repository;

import com.finalproject.filmweb.movie.entity.FavoriteMoviesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FavoriteMoviesRepository extends JpaRepository<FavoriteMoviesEntity, Long> {
    @Query(value = "SELECT f.movie_id,f.user_id,f.id FROM favorites f JOIN users u ON f.user_id =u.id WHERE u.id=:userId ",
            nativeQuery = true)
    List<FavoriteMoviesEntity> findByUserId(Long userId);

}
