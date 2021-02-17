package com.finalproject.filmweb.movie.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieCommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query(value = "SELECT f.movie_id ,f.text,f.user_id,f.created,f.id FROM comments f JOIN movies m ON f.movie_id=m.id  WHERE m.imdbid =:imdbId",
            nativeQuery = true)
    List<CommentEntity> findByMovieId(String imdbId);
}
