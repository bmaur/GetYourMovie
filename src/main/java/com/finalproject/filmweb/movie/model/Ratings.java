package com.finalproject.filmweb.movie.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Ratings {

    @JsonProperty("Source")
    private String source;
    @JsonProperty("Value")
    private String value;


    @Override
    public String toString() {
        return "Ratings{" +
                "source='" + source + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}

