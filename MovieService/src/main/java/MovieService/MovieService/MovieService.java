package MovieService.MovieService;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository){
        this.movieRepository = movieRepository;
    }

    Flux<Movie> allMovies(){
        return movieRepository.findAll();
    }

    Mono<Movie> getMovie(String movieId){
        return movieRepository.findById(movieId);
    }

}
