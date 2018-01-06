package reservationservice.reservationservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.stream.Stream;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@EnableDiscoveryClient
@SpringBootApplication
public class ReservationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationServiceApplication.class, args);
	}

	@Bean
	ApplicationRunner runner(ReservationRepository reservationRepository){
		return args -> {
			Stream.of("Anh Khue", "Dao Tuan", "Vu Cuong", "Quang Nhat", "Kim Bao", "Tung Nguyen")
					.forEach(reservation -> System.out.println(reservationRepository.save(new Reservation(null, reservation))));
		};
	}

	@Bean
    @RefreshScope
	RouterFunction<?> routes(ReservationRepository reservationRepository, @Value("${message}") String message){
		return RouterFunctions.route(GET("/reservations"), request -> {
			return ok().body(BodyInserters.fromObject(reservationRepository.findAll()));
		}).andRoute(GET("/reservation/{id}"), request -> {
			return ok().body(BodyInserters.fromObject(reservationRepository.findById(Long.parseLong(request.pathVariable("id")))));
		}).andRoute(GET("/message"), request -> {
		   return ok().body(BodyInserters.fromObject(message));
        });
	}
}
