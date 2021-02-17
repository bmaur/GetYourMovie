package com.finalproject.filmweb.omdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class OmdbService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public Optional<OmdbMovieResponse> findOmdbMovie(String movieTitle) {
        String uri = String.format("http://www.omdbapi.com/?s=%s&apiKey=b8c8a4e5", movieTitle);

        OmdbMovieResponse movieResponse = null;
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        try {
            movieResponse = objectMapper.readValue(result, OmdbMovieResponse.class);
            log.info(movieResponse.toString());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (movieResponse.isCorrect() && !movieResponse.getSearches().isEmpty()) {
            return Optional.of(movieResponse);
        }

        return Optional.empty();
    }

    public OmdbMovieDetails showMovie(String movieImdbId) {
        String uri = String.format("http://www.omdbapi.com/?i=%s&apiKey=b8c8a4e5", movieImdbId);

        ResponseEntity<OmdbMovieDetails> result = restTemplate.exchange(uri, HttpMethod.GET, null, OmdbMovieDetails.class);
        log.info("Omdb response: {} ", result.getBody());
        checkMovieInformation(Objects.requireNonNull(result.getBody()));
        return result.getBody();
    }

    private OmdbMovieDetails checkMovieInformation(OmdbMovieDetails movieDetails) {
        if (movieDetails.getActors().equals("N/A")) {
            movieDetails.setActors("No actors information");
        }
        if (movieDetails.getAwards().equals("N/A")) {
            movieDetails.setAwards("No awards information");
        }
        if (movieDetails.getDirector().equals("N/A")) {
            movieDetails.setDirector("No director information");
        }
        if (movieDetails.getReleased().equals("N/A")) {
            movieDetails.setReleased("No released information");
        }
        if (movieDetails.getWriter().equals("N/A")) {
            movieDetails.setWriter("No screenplay information");
        }
        if (movieDetails.getYear().equals("N/A")) {
            movieDetails.setYear("No year information");
        }
        if (movieDetails.getPlot().equals("N/A")) {
            movieDetails.setPlot("No plot information ");
        }
        if (movieDetails.getGenre().equals("N/A")) {
            movieDetails.setGenre("No genre information");
        }
        if (movieDetails.getCountry().equals("N/A")) {
            movieDetails.setCountry("No production information");
        }
        return movieDetails;
    }
}
