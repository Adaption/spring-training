package MovieService.MovieService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.UUID;
import java.util.stream.Stream;

import static java.util.UUID.randomUUID;

@SpringBootApplication
public class MovieServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner demo(MovieRepository movieRepository){
		return args -> {
			movieRepository.deleteAll().subscribe(null, null, () -> {
				Stream.of("SOE596", "SNIS193", "MIDE124", "SMD144", "MKDS99", "SKY157", "JUX501")
						.forEach(name -> movieRepository.save(new Movie(UUID.randomUUID().toString(), name)).subscribe(movie -> System.out.println(movie.toString())));
			});

		};
	}

	@Bean
	RouterFunction<?> routerFunction(MovieService movieService){
		return RouterFunctions.route(RequestPredicates.GET("/movies"), request -> {
			return ServerResponse.ok().body(movieService.allMovies(), Movie.class);
		}).andRoute(RequestPredicates.GET("/movie/{id}"), request -> {
			return ServerResponse.ok().body(movieService.getMovie(request.pathVariable("id")), Movie.class);
		});
	}
}
