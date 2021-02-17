package com.finalproject.filmweb.movie.comment;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class Comment {
    String userName;
    String text;
    LocalDate commentCreated;

}
