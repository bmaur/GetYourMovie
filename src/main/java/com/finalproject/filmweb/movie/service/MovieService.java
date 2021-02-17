package com.finalproject.filmweb.movie.service;

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
import com.finalproject.filmweb.movie.repository.FavoriteMoviesRepository;
import com.finalproject.filmweb.movie.repository.MovieRepository;
import com.finalproject.filmweb.movie.repository.MovieToWatchRepository;
import com.finalproject.filmweb.omdb.OmdbMovieDetails;
import com.finalproject.filmweb.omdb.OmdbMovieResponse;
import com.finalproject.filmweb.omdb.OmdbService;
import com.finalproject.filmweb.user.UserEntity;
import com.finalproject.filmweb.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final CommentService movieCommentService;
    private final UserRepository userRepository;
    private final FavoriteMoviesRepository favoriteMoviesRepository;
    private final MovieToWatchRepository movieToWatchRepository;
    private final OmdbService omdbService;
    private final int PAGE_SIZE = 5;

    public Pair<List<MovieSearch>, List<Integer>> findMovie(MovieTitle title, int pageNumber) {
        List<MovieSearch> omdbMovieResponse = omdbService.findOmdbMovie(title.getMovieTitle())
                .map(OmdbMovieResponse::getSearches)
                .orElse(Collections.emptyList());

        omdbMovieResponse.forEach(movieSearch -> {
            MovieSearchEntity movieSearchEntity = movieRepository.findByImdbID(movieSearch.getImdbID())
                    .orElse(new MovieSearchEntity());
            movieSearchEntity.setTitle(movieSearch.getTitle());
            movieSearchEntity.setYear(movieSearch.getYear());
            movieSearchEntity.setImdbID(movieSearch.getImdbID());
            movieSearchEntity.setType(movieSearch.getType());
            movieSearchEntity.setPoster(movieSearch.getPosterURL());
            movieRepository.save(movieSearchEntity);
        });

        int lastRecord = pageNumber * PAGE_SIZE;
        int firstRecord = lastRecord - PAGE_SIZE;
        ArrayList<Integer> pageIndicators = new ArrayList<>();
        int pageIndicator = 1;
        for (int i = 1; i <= omdbMovieResponse.size(); i = i + PAGE_SIZE) {
            pageIndicators.add(pageIndicator);
            pageIndicator++;
        }
        if (lastRecord >= omdbMovieResponse.size()) {
            lastRecord = omdbMovieResponse.size();
        }
        return Pair.of(omdbMovieResponse.subList(firstRecord, lastRecord), pageIndicators);
    }

    public MovieDetails showMovie(String movieImdbId, String userName) {
        List<Comment> comments = movieCommentService.getMovieComments(movieImdbId);
        boolean isFavourite = false;
        boolean isToWatch = false;
        if (userName != null) {
            isFavourite = isFavouriteMovie(movieImdbId, userName);
            isToWatch = isToWatch(movieImdbId, userName);
        }
        return mapToMovieDetails(movieImdbId, omdbService.showMovie(movieImdbId), comments, isFavourite, isToWatch);
    }

    private MovieDetails mapToMovieDetails(String movieImdbId, OmdbMovieDetails result, List<Comment> comments,
                                           boolean isFavourite, boolean isToWatch) {
        MovieDetails movieDetails = MovieDetails.builder()
                .imdbId(movieImdbId)
                .title(result.getTitle())
                .year(result.getYear())
                .rated(result.getRated())
                .released(result.getReleased())
                .genre(result.getGenre())
                .director(result.getDirector())
                .writer(result.getWriter())
                .actors(result.getActors())
                .plot(result.getPlot())
                .language(result.getLanguage())
                .country(result.getCountry())
                .awards(result.getAwards())
                .poster_url(result.getPoster_url())
                .ratings(result.getRatings())
                .type(result.getType())
                .dvd(result.getDvd())
                .boxOffice(result.getBoxOffice())
                .production(result.getProduction())
                .comments(comments)
                .isMyFavoritesMovies(isFavourite)
                .isMyToWatchMovies(isToWatch)
                .build();
        return movieDetails;
    }

    private boolean isFavouriteMovie(String movieImdbId, String userName) {
        Optional<MovieSearchEntity> movie = movieRepository.findByImdbID(movieImdbId);
        UserEntity userEntity = userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        return favoriteMoviesRepository.findByUserId(userEntity.getId())
                .stream()
                .filter(p -> p.getUser().getId().equals(userEntity.getId()))
                .anyMatch(str -> str.getMovie().getId().equals(movie.get().getId()));
    }

    private boolean isToWatch(String movieImdbId, String userName) {
        Optional<MovieSearchEntity> movie = movieRepository.findByImdbID(movieImdbId);
        UserEntity userEntity = userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        return movieToWatchRepository.findByUserId(userEntity.getId())
                .stream()
                .filter(p -> p.getUser().getId().equals((userEntity.getId())))
                .anyMatch(str -> str.getMovie().getId().equals(movie.get().getId()));
    }

    public void addToFavorites(String ImdbId, String userEmail) {
        UserEntity userEntity = userRepository.findByUserName(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        FavoriteMoviesEntity favoriteMoviesEntity = new FavoriteMoviesEntity();
        Optional<MovieSearchEntity> movie = movieRepository.findByImdbID(ImdbId);

        favoriteMoviesEntity.setMovie(movie
                .orElseThrow(() -> new RuntimeException("Movie not found")));

        if (favoriteMoviesRepository.findByUserId(userEntity.getId())
                .stream()
                .filter(p -> p.getUser().getId().equals(userEntity.getId()))
                .collect(toList())
                .stream()
                .anyMatch(str -> str.getMovie().getId().equals(movie.get().getId()))) {
            throw new MovieAlreadyExistInFavoritesException();
        }
        favoriteMoviesEntity.setUser(userEntity);
        favoriteMoviesRepository.save(favoriteMoviesEntity);
    }

    private List<MovieSearch> getFavoriteMovies(Long userId) {
        List<FavoriteMoviesEntity> favMovies = favoriteMoviesRepository.findByUserId(userId);
        return favMovies
                .stream()
                .map(favoriteMoviesEntity -> {
                    MovieSearch myFavoriteMovies = new MovieSearch();
                    myFavoriteMovies.setTitle(favoriteMoviesEntity.getMovie().getTitle());
                    myFavoriteMovies.setYear(favoriteMoviesEntity.getMovie().getYear());
                    myFavoriteMovies.setPosterURL(favoriteMoviesEntity.getMovie().getPoster());
                    myFavoriteMovies.setImdbID(favoriteMoviesEntity.getMovie().getImdbID());
                    myFavoriteMovies.setType(favoriteMoviesEntity.getMovie().getType());
                    return myFavoriteMovies;
                })
                .collect(toList());
    }

    public Pair<List<MovieSearch>, List<Integer>> getFavoriteMoviesWithPages(int pageNumber, Long userId) {
        int lastRecord = pageNumber * PAGE_SIZE;
        int firstRecord = lastRecord - PAGE_SIZE;
        ArrayList<Integer> pageIndicators = new ArrayList<>();
        List<MovieSearch> favMovies = getFavoriteMovies(userId);
        int pageIndicator = 1;
        for (int i = 1; i <= favMovies.size(); i = i + PAGE_SIZE) {
            pageIndicators.add(pageIndicator);
            pageIndicator++;
        }
        if (lastRecord >= favMovies.size()) {
            lastRecord = favMovies.size();
        }
        return Pair.of(favMovies.subList(firstRecord, lastRecord), pageIndicators);
    }


    public void addToWatch(String ImdbId, String userEmail) {
        UserEntity userEntity = userRepository.findByUserName(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        MovieSearchEntity movie = movieRepository.findByImdbID(ImdbId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        List<Long> toWatchMovieIds =  movieToWatchRepository.findByUserId(userEntity.getId())
                .stream()
                .map(entity -> entity.getMovie().getId()).collect(toList());
        if (toWatchMovieIds.contains(movie.getId())) {
            throw new MovieAlreadyExistInToWatchException();
        }

        MovieToWatchEntity movieToWatchEntity = new MovieToWatchEntity();
        movieToWatchEntity.setMovie(movie);
        movieToWatchEntity.setUser(userEntity);
        movieToWatchRepository.save(movieToWatchEntity);
    }

    private List<MovieSearch> getMoviesToWatch(Long userId) {
        List<MovieToWatchEntity> toWatch = movieToWatchRepository.findByUserId(userId);
        return toWatch
                .stream()
                .map(movieToWatchEntity -> {
                    MovieSearch moviesToWatch = new MovieSearch();
                    moviesToWatch.setTitle(movieToWatchEntity.getMovie().getTitle());
                    moviesToWatch.setYear(movieToWatchEntity.getMovie().getYear());
                    moviesToWatch.setType(movieToWatchEntity.getMovie().getType());
                    moviesToWatch.setPosterURL(movieToWatchEntity.getMovie().getPoster());
                    moviesToWatch.setImdbID(movieToWatchEntity.getMovie().getImdbID());
                    return moviesToWatch;
                })
                .collect(toList());
    }

    public Pair<List<MovieSearch>, List<Integer>> getMoviesToWatchWithPages(int pageNumber, Long userId) {
        int lastRecord = pageNumber * PAGE_SIZE;
        int firstRecord = lastRecord - PAGE_SIZE;
        ArrayList<Integer> pageIndicators = new ArrayList<>();
        List<MovieSearch> watchMovie = getMoviesToWatch(userId);
        int pageIndicator = 1;
        for (int i = 1; i <= watchMovie.size(); i = i + PAGE_SIZE) {
            pageIndicators.add(pageIndicator);
            pageIndicator++;
        }
        if (lastRecord >= watchMovie.size()) {
            lastRecord = watchMovie.size();
        }
        return Pair.of(watchMovie.subList(firstRecord, lastRecord), pageIndicators);

    }

}




