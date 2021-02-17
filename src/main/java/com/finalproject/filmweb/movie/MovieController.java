package com.finalproject.filmweb.movie;

import com.finalproject.filmweb.movie.comment.Comment;
import com.finalproject.filmweb.movie.comment.CommentService;
import com.finalproject.filmweb.movie.model.MovieSearch;
import com.finalproject.filmweb.movie.model.MovieTitle;
import com.finalproject.filmweb.movie.service.MovieService;
import com.finalproject.filmweb.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final CommentService commentService;
    private final UserRepository userRepository;

    @RequestMapping(value = "/movies", method = RequestMethod.GET)
    public String movieSearch(Principal principal, Model model, @ModelAttribute("title") String title, @RequestParam(value = "page", required = false) Integer pageNumber) {
        MovieTitle movieTitle = new MovieTitle();
        title = title.trim();
        movieTitle.setMovieTitle(title);
        Pair<List<MovieSearch>, List<Integer>> paginationPari = movieService.findMovie(movieTitle, (pageNumber == null || pageNumber == 0) ? 1 : pageNumber);
        List<MovieSearch> movies = paginationPari.getFirst();
        if (movies.isEmpty() || title.equals("")) {
            return principal == null ? "moviesNotFoundAnonymous" : "moviesNotFound";
        }
        model.addAttribute("movies", movies);
        model.addAttribute("pages", paginationPari.getSecond());
        model.addAttribute("title", title);
        return principal == null ? "moviesAnonymous" : "movies";
    }


    @RequestMapping(value = "/movie/details/{imdbId}", method = RequestMethod.GET)
    public String movieDetails(Principal principal, Model model, @PathVariable String imdbId) {
        if (principal == null) {
            model.addAttribute("movieDetails", movieService.showMovie(imdbId, null));
            return "movieDetailsAnonymous";
        }
        model.addAttribute("movieDetails", movieService.showMovie(imdbId, principal.getName()));
        return "movieDetails";
    }

    @RequestMapping(value = "/movie/details/{imdbId}/comment", method = RequestMethod.POST)
    public String addCommentForMovie(Principal principal, Model model, @PathVariable String imdbId, String commentText) {
        if (commentText.equals("")) {
            return movieDetails(principal, model, imdbId);
        }
        Comment comment = Comment.builder()
                .text(commentText)
                .commentCreated(LocalDate.now())
                .build();
        commentService.addComment(comment, imdbId, principal.getName());
        return movieDetails(principal, model, imdbId);
    }

    @RequestMapping(value = "/movie/details/{imdbId}/fav", method = RequestMethod.POST)
    public String addMovieToFavorites(Principal principal, Model model, @PathVariable String imdbId) {
        movieService.addToFavorites(imdbId, principal.getName());
        return movieDetails(principal, model, imdbId);
    }

    @RequestMapping(value = "/myFavoriteMovies", method = RequestMethod.GET)
    public String showFavoriteMovies(Principal principal, Model model, @RequestParam(value = "page", required = false) Integer pageNumber) {
        Long user = userRepository.findByUserName(principal.getName()).get().getId();
        Pair<List<MovieSearch>, List<Integer>> paginationPari = movieService.getFavoriteMoviesWithPages((pageNumber == null || pageNumber == 0) ? 1 : pageNumber, user);
        List<MovieSearch> movies = paginationPari.getFirst();
        model.addAttribute("myFav", movies);
        model.addAttribute("pages", paginationPari.getSecond());
        model.addAttribute("user", user);
        return "myFavoriteMovies";
    }

    @RequestMapping(value = "/movie/details/{imdbId}/towatch", method = RequestMethod.GET)
    public String addMovieToWatch(Principal principal, Model model, @PathVariable String imdbId) {
        movieService.addToWatch(imdbId, principal.getName());
        return movieDetails(principal, model, imdbId);
    }

    @RequestMapping(value = "/myMoviesToWatch", method = RequestMethod.GET)
    public String showMoviesToWatch(Principal principal, Model model, @RequestParam(value = "page", required = false) Integer pageNumber) {
        Long user = userRepository.findByUserName(principal.getName()).get().getId();
        Pair<List<MovieSearch>, List<Integer>> paginationPari = movieService.getMoviesToWatchWithPages((pageNumber == null || pageNumber == 0) ? 1 : pageNumber, user);
        List<MovieSearch> movies = paginationPari.getFirst();
        model.addAttribute("myToWatch", movies);
        model.addAttribute("pages", paginationPari.getSecond());
        model.addAttribute("user", user);
        return "myMoviesToWatch";
    }
}






























