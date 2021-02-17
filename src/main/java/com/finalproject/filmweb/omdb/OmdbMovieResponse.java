package com.finalproject.filmweb.omdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.finalproject.filmweb.movie.model.MovieSearch;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OmdbMovieResponse {

    @JsonProperty("Search")
    private List<MovieSearch> searches;

    @JsonProperty("Response")
    private boolean correct;


    @Override
    public String toString() {
        return "MovieResponse{" +
                "searches=" + searches +
                '}';
    }

}
