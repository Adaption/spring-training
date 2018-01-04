package MovieService.MovieService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
			Stream.of("SOE596", "SNIS193", "MIDE124", "SMD144", "MKDS99", "SKY157", "JUX501")
					.map(name -> new Movie(randomUUID().toString(), name))
					.forEach(System.out::println);
		};
	}
}
