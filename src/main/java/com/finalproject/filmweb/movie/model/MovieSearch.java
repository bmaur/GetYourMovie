package com.finalproject.filmweb.movie.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MovieSearch {
    @JsonProperty("Title")
    private String title;
    @JsonProperty("Year")
    private String year;
    @JsonProperty("imdbID")
    private String imdbID;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("Poster")
    private String posterURL;



    @Override
    public String toString() {
        return "MovieSearch{" +
                "title='" + title + '\'' +
                ", year=" + year +
                ", imdbID='" + imdbID + '\'' +
                ", type='" + type + '\'' +
                ", posterURL='" + posterURL + '\'' +
                '}';
    }

}
