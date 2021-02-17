package com.finalproject.filmweb.movie;

import com.finalproject.filmweb.movie.comment.Comment;
import com.finalproject.filmweb.movie.comment.CommentService;
import com.finalproject.filmweb.movie.entity.FavoriteMoviesEntity;
import com.finalproject.filmweb.movie.entity.MovieSearchEntity;
import com.finalproject.filmweb.movie.entity.MovieToWatchEntity;
import com.finalproject.filmweb.movie.exception.MovieAlreadyExistInFavoritesException;
import com.finalproject.filmweb.movie.exception.MovieAlreadyExistInToWatchException;
import com.finalproject.filmweb.movie.model.MovieDetails;
import com.finalproject.filmweb.movie.model.MovieSearch;
import com.finalproject.filmweb.movie.model.MovieTitle;
import com.finalproject.filmweb.movie.model.Ratings;
import com.finalproject.filmweb.movie.repository.FavoriteMoviesRepository;
import com.finalproject.filmweb.movie.repository.MovieRepository;
import com.finalproject.filmweb.movie.repository.MovieToWatchRepository;
import com.finalproject.filmweb.movie.service.MovieService;
import com.finalproject.filmweb.omdb.OmdbMovieDetails;
import com.finalproject.filmweb.omdb.OmdbMovieResponse;
import com.finalproject.filmweb.omdb.OmdbService;
import com.finalproject.filmweb.user.UserEntity;
import com.finalproject.filmweb.user.UserRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class MovieServiceTest {

    private MovieService sut;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private CommentService movieCommentService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FavoriteMoviesRepository favoriteMoviesRepository;
    @Mock
    private MovieToWatchRepository movieToWatchRepository;
    @Mock
    private OmdbService omdbService;

    @BeforeEach
    void setUp() {
        initMocks(this);
        sut = new MovieService(movieRepository, movieCommentService, userRepository, favoriteMoviesRepository, movieToWatchRepository, omdbService);
    }

    private MovieTitle movieTitleSetUp() {
        MovieTitle movieTitle = new MovieTitle();
        movieTitle.setMovieTitle("TestMovieTitle");
        return movieTitle;
    }

    private MovieSearchEntity movieSearchEntitySetUp(MovieTitle movieTitle) {
        MovieSearchEntity movieSearchEntity = new MovieSearchEntity();
        movieSearchEntity.setTitle(movieTitle.getMovieTitle());
        movieSearchEntity.setId(1L);
        movieSearchEntity.setYear("TestMovieYear");
        movieSearchEntity.setImdbID("TestMovieID");
        movieSearchEntity.setType("TestMovieType");
        movieSearchEntity.setPoster("TestMoviePoster");
        return movieSearchEntity;
    }

    private UserEntity userEntitySetUp() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId((long) 1);
        userEntity.setUserName("TestUserName");
        userEntity.setUserNick("TestUserNick");
        userEntity.setPassword("TestUserPassword");
        userEntity.setEnabled(true);
        userEntity.setConfirmationToken("TestConfirmationToken");
        userEntity.setCreatedOn(LocalDateTime.now());
        return userEntity;
    }


    @Captor
    ArgumentCaptor<MovieSearchEntity> movieSearchEntityArgumentCaptor;

    @Test
    void shouldFindMovieCorrectly() {
        //given
        MovieSearch movieSearch = new MovieSearch();
        movieSearch.setImdbID("TestMovieID");
        movieSearch.setTitle("TestMovieTitle");
        movieSearch.setYear("TestMovieYear");
        movieSearch.setPosterURL("TestMoviePoster");
        movieSearch.setType("TestMovieType");
        int pageNumber = 1;

        OmdbMovieResponse omdbMovieResponse = new OmdbMovieResponse();
        omdbMovieResponse.setSearches(Collections.singletonList(movieSearch));

        when(omdbService.findOmdbMovie(movieTitleSetUp().getMovieTitle())).thenReturn(Optional.of(omdbMovieResponse));
        when(movieRepository.findByImdbID(movieSearch.getImdbID())).thenReturn(Optional.of(movieSearchEntitySetUp(movieTitleSetUp())));

        //when
        final Pair<List<MovieSearch>, List<Integer>> result = sut.findMovie(movieTitleSetUp(), pageNumber);

        verify(movieRepository, times(1)).findByImdbID(movieSearchEntitySetUp(movieTitleSetUp()).getImdbID());
        verify(movieRepository, times(1)).save(movieSearchEntityArgumentCaptor.capture());

        MovieSearchEntity capturedMovieSearch = movieSearchEntityArgumentCaptor.getValue();


        //then
        assertThat(capturedMovieSearch.getTitle()).isEqualTo(movieSearchEntitySetUp(movieTitleSetUp()).getTitle());
        assertThat(capturedMovieSearch.getImdbID()).isEqualTo(movieSearchEntitySetUp(movieTitleSetUp()).getImdbID());
        assertThat(result.getFirst().get(0).getTitle()).isEqualTo(capturedMovieSearch.getTitle());

    }

    @Test
    void shouldShowMovieCorrectly() {
        //given
        String movieImdbId = "TestMovieID";
        String userName = "TestUserName";
        String userComment = "TestComment";
        Comment commentExample = Comment.builder().userName(userName).text(userComment).commentCreated(LocalDate.now()).build();

        Ratings ratings = new Ratings();
        ratings.setValue("TestValue");
        ratings.setSource("TestSource");

        OmdbMovieDetails omdbMovieDetails = new OmdbMovieDetails();
        omdbMovieDetails.setTitle("TestTitle");
        omdbMovieDetails.setYear("TestYear");
        omdbMovieDetails.setRated("TestRated");
        omdbMovieDetails.setReleased("TestReleased");
        omdbMovieDetails.setGenre("TestGenre");
        omdbMovieDetails.setDirector("TestDirector");
        omdbMovieDetails.setWriter("TestWriter");
        omdbMovieDetails.setActors("TestActors");
        omdbMovieDetails.setPlot("TestPlot");
        omdbMovieDetails.setLanguage("TestLanguage");
        omdbMovieDetails.setCountry("TestCountry");
        omdbMovieDetails.setAwards("TestAwards");
        omdbMovieDetails.setPoster_url("TestPoster");
        omdbMovieDetails.setRatings(Collections.singletonList(ratings));
        omdbMovieDetails.setType("TestType");
        omdbMovieDetails.setDvd("TestDVD");
        omdbMovieDetails.setBoxOffice("TestBoxOffice");
        omdbMovieDetails.setProduction("TestProduction");

        MovieDetails movieDetails = MovieDetails.builder().
                imdbId(movieImdbId).title("TestTitle").year("TestYear").rated("TestRated").
                released("TestReleased").genre("TestGenre").director("TestDirector").writer("TestWriter").
                actors("TestActors").plot("TestPlot").language("TestLanguage").country("TestCountry").
                awards("TestAwards").poster_url("TestPoster").ratings(Collections.singletonList(ratings)).
                type("TestType").dvd("TestDVD").boxOffice("TestBoxOffice").production("TestProduction").
                comments(Collections.singletonList(commentExample)).isMyFavoritesMovies(false).
                isMyToWatchMovies(false).
                build();

        when(movieCommentService.getMovieComments(movieImdbId)).thenReturn(Collections.singletonList(commentExample));
        when(movieRepository.findByImdbID(movieImdbId)).thenReturn(Optional.of(movieSearchEntitySetUp(movieTitleSetUp())));
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(userEntitySetUp()));
        when(omdbService.showMovie(movieImdbId)).thenReturn(omdbMovieDetails);

        //when
        final MovieDetails result = sut.showMovie(movieImdbId, userName);

        verify(favoriteMoviesRepository, times(1)).findByUserId(userEntitySetUp().getId());
        verify(movieToWatchRepository, times(1)).findByUserId(userEntitySetUp().getId());

        //then
        assertThat(result).isEqualTo(movieDetails);

    }

    @Captor
    ArgumentCaptor<FavoriteMoviesEntity> favoriteMoviesEntityArgumentCaptor;

    @Test
    void shouldAddMovieToFavoritesCorrectly() {
        //given
        String userName = "TestUserName";
        String movieImdbId = "TestMovieID";
        MovieSearchEntity movieSearchEntitySetUp = movieSearchEntitySetUp(movieTitleSetUp());
        UserEntity userEntity = userEntitySetUp();

        FavoriteMoviesEntity favoriteMoviesEntity = new FavoriteMoviesEntity();

        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(movieRepository.findByImdbID(movieImdbId)).thenReturn(Optional.of(movieSearchEntitySetUp));
        favoriteMoviesEntity.setUser(userEntity);
        favoriteMoviesEntity.setMovie(movieSearchEntitySetUp);

        //when
        sut.addToFavorites(movieImdbId, userName);

        verify(favoriteMoviesRepository, times(1)).save(favoriteMoviesEntityArgumentCaptor.capture());
        FavoriteMoviesEntity favoriteResult = favoriteMoviesEntityArgumentCaptor.getValue();

        //then
        assertThat(favoriteMoviesEntity.getMovie()).isEqualTo(favoriteResult.getMovie());
        assertThat(favoriteMoviesEntity.getUser()).isEqualTo(favoriteResult.getUser());

    }

    @Test
    void shouldThrowsRuntimeExceptionWhenUserIsNotFoundWhenAddToFavorites() {
        //given
        String userName = "RandomUserName";
        String movieImdbId = "TestMovieID";
        UserEntity userEntityNotFound = new UserEntity();
        final String errorMessage = "User not found";

        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(userEntityNotFound));
        //when
        final Throwable result = Assertions.assertThrows(RuntimeException.class,
                () -> sut.addToFavorites(movieImdbId, userName));

        verify(favoriteMoviesRepository, times(0)).findByUserId(any());
        verify(favoriteMoviesRepository, times(0)).save(any());
        //then
        assertThat(result.getMessage().equals(errorMessage));
    }

    @Test
    void shouldThrowsRunTimeExceptionWhenMovieIsNotFoundWhenAddToFavorites() {
        //given
        String userName = "TestUserName";
        String movieImdbId = "TestMovieID";
        final String errorMessage = "Movie not found";

        UserEntity userEntity = userEntitySetUp();

        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        //when
        final Throwable result = Assertions.assertThrows(RuntimeException.class,
                () -> sut.addToFavorites(movieImdbId, userName));

        verify(favoriteMoviesRepository, times(0)).findByUserId(any());
        verify(favoriteMoviesRepository, times(0)).save(any());
        //then
        assertThat(result.getMessage().equals(errorMessage));

    }

    @Test
    void shouldThrowsMovieAlreadyExistInFavoritesExceptionWhenAddToFavorites() {
        //given
        String userName = "TestUserName";
        String movieImdbId = "TestMovieID";
        final String errorMessage = "The movie already exists in favorites";

        UserEntity userEntity = userEntitySetUp();

        MovieSearchEntity movieSearchEntity = movieSearchEntitySetUp(movieTitleSetUp());

        FavoriteMoviesEntity favoriteMoviesEntity = new FavoriteMoviesEntity();
        favoriteMoviesEntity.setUser(userEntity);
        favoriteMoviesEntity.setMovie(movieSearchEntity);

        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(movieRepository.findByImdbID(movieImdbId)).thenReturn(Optional.of(movieSearchEntity));
        when(favoriteMoviesRepository.findByUserId(userEntity.getId())).thenReturn(Collections.singletonList(favoriteMoviesEntity));
        //when
        final Throwable result = Assertions.assertThrows(MovieAlreadyExistInFavoritesException.class,
                () -> sut.addToFavorites(movieImdbId, userName));
        verify(favoriteMoviesRepository, times(0)).save(any());
        //then
        assertThat(result.getMessage().equals(errorMessage));
    }

    @Test
    void shouldGetFavoritesMoviesCorrectly() {
        //given
        Long userId = 1L;
        int pageNumber = 1;
        UserEntity userEntity = userEntitySetUp();
        MovieSearchEntity movieSearchEntity = movieSearchEntitySetUp(movieTitleSetUp());

        FavoriteMoviesEntity favoriteMoviesEntity = new FavoriteMoviesEntity();
        favoriteMoviesEntity.setUser(userEntity);
        favoriteMoviesEntity.setMovie(movieSearchEntity);

        when(favoriteMoviesRepository.findByUserId(userId)).thenReturn(Collections.singletonList(favoriteMoviesEntity));

        MovieSearch movieSearch = new MovieSearch();
        movieSearch.setTitle(movieSearchEntity.getTitle());
        movieSearch.setYear(movieSearchEntity.getYear());
        movieSearch.setPosterURL(movieSearchEntity.getPoster());
        movieSearch.setImdbID(movieSearchEntity.getImdbID());
        movieSearch.setType(movieSearchEntity.getType());

        //when

        final Pair<List<MovieSearch>, List<Integer>> result = sut.getFavoriteMoviesWithPages(pageNumber, userId);
        //then
        assertThat(result.getFirst().get(0).getTitle()).isEqualTo(movieSearch.getTitle());
        assertThat(result.getFirst().get(0).getImdbID()).isEqualTo(movieSearch.getImdbID());
        assertThat(result.getSecond().get(0)).isEqualTo(pageNumber);

    }

    @Captor
    ArgumentCaptor<MovieToWatchEntity> movieToWatchEntityArgumentCaptor;

    @Test
    void shouldAddToWatchCorrectly() {
        //given
        String userName = "TestUserName";
        String movieImdbId = "TestMovieID";

        UserEntity userEntity = userEntitySetUp();

        MovieSearchEntity movieSearchEntity = movieSearchEntitySetUp(movieTitleSetUp());

        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(movieRepository.findByImdbID(movieImdbId)).thenReturn(Optional.of(movieSearchEntity));

        MovieToWatchEntity movieToWatchEntity = new MovieToWatchEntity();
        movieToWatchEntity.setMovie(movieSearchEntity);
        movieToWatchEntity.setUser(userEntity);

        //when
        sut.addToWatch(movieImdbId, userName);
        verify(movieToWatchRepository, times(1)).save(movieToWatchEntityArgumentCaptor.capture());
        MovieToWatchEntity result = movieToWatchEntityArgumentCaptor.getValue();

        //then
        assertThat(result.getMovie()).isEqualTo(movieToWatchEntity.getMovie());
        assertThat(result.getUser()).isEqualTo(movieToWatchEntity.getUser());

    }

    @Test
    void shouldThrowsRuntimeExceptionWhenUserIsNotFoundWhenAddToWatch() {
        //given
        String userName = "RandomUserName";
        String movieImdbId = "TestMovieID";
        UserEntity userEntityNotFound = new UserEntity();
        final String errorMessage = "User not found";

        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(userEntityNotFound));
        //when
        final Throwable result = Assertions.assertThrows(RuntimeException.class,
                () -> sut.addToWatch(movieImdbId, userName));

        verify(movieToWatchRepository, times(0)).save(any());
        //then
        assertThat(result.getMessage().equals(errorMessage));
    }

    @Test
    void shouldThrowsRunTimeExceptionWhenMovieIsNotFoundWhenAddToWatch() {
        //given
        String userName = "TestUserName";
        String movieImdbId = "TestMovieID";
        final String errorMessage = "Movie not found";

        UserEntity userEntity = userEntitySetUp();

        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        //when
        final Throwable result = Assertions.assertThrows(RuntimeException.class,
                () -> sut.addToWatch(movieImdbId, userName));

        verify(movieToWatchRepository, times(0)).save(any());
        //then
        assertThat(result.getMessage().equals(errorMessage));

    }

    @Test
    void shouldThrowsMovieAlreadyExistInToWatchExceptionWhenAddToWatch() {
        //given
        Long userId = 1L;
        String userName = "TestUserName";
        String movieImdbId = "TestMovieID";
        final String errorMessage = "The movie already exists in to watch section";

        UserEntity userEntity = userEntitySetUp();
        MovieSearchEntity movieSearchEntity = movieSearchEntitySetUp(movieTitleSetUp());

        MovieToWatchEntity movieToWatchEntity = new MovieToWatchEntity();
        movieToWatchEntity.setUser(userEntity);
        movieToWatchEntity.setMovie(movieSearchEntity);
        List<MovieToWatchEntity> movies = Collections.singletonList(movieToWatchEntity);

        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(movieRepository.findByImdbID(movieImdbId)).thenReturn(Optional.of(movieSearchEntity));
        when(movieToWatchRepository.findByUserId(userId)).thenReturn(movies);

        //when
        final Throwable result = Assertions.assertThrows(MovieAlreadyExistInToWatchException.class,
                () -> sut.addToWatch(movieImdbId, userName));

        //then
        verify(movieToWatchRepository, times(0)).save(any());
        assertThat(result.getMessage().equals(errorMessage));
    }

    @Test
    void shouldGetMoviesToWatchWithPagesCorrectly() {
        //given
        Long userId = 1L;
        int pageNumber = 1;
        UserEntity userEntity = userEntitySetUp();

        MovieTitle movieTitle = new MovieTitle();
        movieTitle.setMovieTitle("TestMovieTitle");

        MovieSearchEntity movieSearchEntity = movieSearchEntitySetUp(movieTitleSetUp());

        MovieToWatchEntity movieToWatchEntity = new MovieToWatchEntity();
        movieToWatchEntity.setUser(userEntity);
        movieToWatchEntity.setMovie(movieSearchEntity);


        when(movieToWatchRepository.findByUserId(userId)).thenReturn(Collections.singletonList(movieToWatchEntity));
        MovieSearch movieSearch = new MovieSearch();
        movieSearch.setTitle(movieSearchEntity.getTitle());
        movieSearch.setYear(movieSearchEntity.getYear());
        movieSearch.setPosterURL(movieSearchEntity.getPoster());
        movieSearch.setImdbID(movieSearchEntity.getImdbID());
        movieSearch.setType(movieSearchEntity.getType());

        //when
        final Pair<List<MovieSearch>, List<Integer>> result = sut.getMoviesToWatchWithPages(pageNumber, userId);
        //then
        assertThat(result.getFirst().get(0).getTitle()).isEqualTo(movieSearch.getTitle());
        assertThat(result.getFirst().get(0).getImdbID()).isEqualTo(movieSearch.getImdbID());
        assertThat(result.getSecond().get(0)).isEqualTo(pageNumber);
    }


}



























