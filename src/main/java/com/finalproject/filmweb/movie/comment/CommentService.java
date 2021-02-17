package com.finalproject.filmweb.movie.comment;

import com.finalproject.filmweb.movie.repository.MovieRepository;
import com.finalproject.filmweb.user.UserEntity;
import com.finalproject.filmweb.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final MovieCommentRepository movieCommentRepository;

    public List<Comment> getMovieComments(String movieId) {
        return movieCommentRepository.findByMovieId(movieId)
                .stream()
                .map(commentEntity ->
                        Comment.builder()
                                .userName(commentEntity.getUser().getUserNick())
                                .text(commentEntity.getText())
                                .commentCreated(commentEntity.getCreated())
                                .build())
                .collect(Collectors.toList());

    }

    public void addComment(Comment comment, String ImdbId, String userEmail) {
        UserEntity userEntity = userRepository.findByUserName(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setUser(userEntity);
        commentEntity.setText(comment.getText());
        commentEntity.setCreated(comment.getCommentCreated());
        commentEntity.setMovie(movieRepository.findByImdbID(ImdbId)
                .orElseThrow(() -> new RuntimeException("Movie not found!")));
        movieCommentRepository.save(commentEntity);
    }
}
