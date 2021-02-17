package com.finalproject.filmweb.omdb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalproject.filmweb.movie.model.MovieSearch;
import com.finalproject.filmweb.movie.model.Ratings;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OmdbServiceTest {

    private OmdbService sut;
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RestTemplate restTemplate;


    @BeforeEach
    void setUp() {
        initMocks(this);
        sut = new OmdbService(objectMapper, restTemplate);
    }

    @SneakyThrows
    @Test
    void findMovieCorrectly() {
        //given
        String movieTitle = "randomMovieTitle";
        String uri = String.format("http://www.omdbapi.com/?s=%s&apiKey=b8c8a4e5", movieTitle);
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        List<MovieSearch> movieSearches = new ArrayList<>();
        MovieSearch movieSearch = new MovieSearch();
        movieSearch.setTitle("randomMovieTitle");
        movieSearch.setImdbID("randomMovieImdb");
        movieSearch.setPosterURL("randomMoviePosterUlr");
        movieSearch.setType("randomMovieType");
        movieSearch.setYear("randomMovieYear");
        movieSearches.add(movieSearch);

        OmdbMovieResponse movieResponse = new OmdbMovieResponse();
        movieResponse.setSearches(movieSearches);
        movieResponse.setCorrect(true);
        when(objectMapper.readValue(result, OmdbMovieResponse.class)).thenReturn(movieResponse);

        //when
        sut.findOmdbMovie(movieTitle);

        //then
        assertThat(movieResponse.getSearches()).isEqualTo(movieSearches);
    }

    @SneakyThrows
    @Test
    void shouldNotFindMovieCorrectly() {
        //given
        String movieTitle = "randomMovieTitle";
        String uri = String.format("http://www.omdbapi.com/?s=%s&apiKey=b8c8a4e5", movieTitle);
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        OmdbMovieResponse movieResponse = new OmdbMovieResponse();
        movieResponse.setCorrect(false);

        when(objectMapper.readValue(result, OmdbMovieResponse.class)).thenReturn(movieResponse);

        //when
        sut.findOmdbMovie(movieTitle);

        //then
        assertThat(movieResponse.getSearches()).isEqualTo(null);
    }

    @Test
    void showMovieCorrectly() {
        //then
        String movieImdbId = "randomMovieImdbId";
        String uri = String.format("http://www.omdbapi.com/?i=%s&apiKey=b8c8a4e5", movieImdbId);

        String source = "randomMovieSource";
        String value = "randomMovieValue";
        Ratings testRatings = new Ratings();
        testRatings.setSource(source);
        testRatings.setValue(value);
        List<Ratings> ratings = new ArrayList<>();
        ratings.add(0, testRatings);

        OmdbMovieDetails omdbMovieDetails = new OmdbMovieDetails();
        omdbMovieDetails.setTitle("randomMovieTitle");
        omdbMovieDetails.setPlot("randomMoviePlot");
        omdbMovieDetails.setCountry("randomMovieCountry");
        omdbMovieDetails.setGenre("randomMovieGenre");
        omdbMovieDetails.setYear("randomMovieYear");
        omdbMovieDetails.setWriter("randomMovieWriter");
        omdbMovieDetails.setReleased("randomMovieReleased");
        omdbMovieDetails.setDirector("randomMovieDirector");
        omdbMovieDetails.setAwards("randomMovieAwards");
        omdbMovieDetails.setActors("randomMovieActors");
        omdbMovieDetails.setBoxOffice("randomMovieBocOffice");
        omdbMovieDetails.setDvd("randomMovieDvd");
        omdbMovieDetails.setLanguage("randomMovieLanguage");
        omdbMovieDetails.setPoster_url("randomPosterUrl");
        omdbMovieDetails.setProduction("randomMovieProduction");
        omdbMovieDetails.setRated("randomMovieRated");
        omdbMovieDetails.setType("randomMovieType");
        omdbMovieDetails.setRatings(ratings);


        when(restTemplate.exchange(anyString(), any(), any(), eq(OmdbMovieDetails.class))).
                thenReturn(ResponseEntity.ok().body((omdbMovieDetails)));


        //when
        OmdbMovieDetails result = sut.showMovie(movieImdbId);

        //then
//        assertThat(result).isNotNull();
        assertThat(result.getActors()).isEqualTo(omdbMovieDetails.getActors());
        assertThat(result.getTitle()).isEqualTo(omdbMovieDetails.getTitle());
        assertThat(result.getType()).isEqualTo(omdbMovieDetails.getType());
        assertThat(result.getDirector()).isEqualTo(omdbMovieDetails.getDirector());
    }


}
