package MovieService.MovieService;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

public class MovieHandler {

    private final MovieService movieService;

    public MovieHandler(MovieService movieService){
        this.movieService = movieService;
    }

    Mono<ServerResponse> allMovies(ServerRequest request){
        return ok().body(movieService.allMovies(), Movie.class);
    }

    Mono<ServerResponse> getMovie(ServerRequest request){
        return ok().body(movieService.getMovie(request.pathVariable("id")), Movie.class);
    }
}
