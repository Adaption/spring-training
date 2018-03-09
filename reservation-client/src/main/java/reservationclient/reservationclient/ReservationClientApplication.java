package reservationclient.reservationclient;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.MessageChannel;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static org.springframework.web.reactive.function.BodyInserters.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@EnableResourceServer
@EnableBinding(ReservationChannels.class)
@EnableFeignClients
@EnableZuulProxy
@IntegrationComponentScan
@EnableCircuitBreaker
@EnableDiscoveryClient
@SpringBootApplication
public class ReservationClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationClientApplication.class, args);
	}

	private final ReservationWriter reservationWriter;

	@HystrixCommand(fallbackMethod = "fallback")
	@Bean
	RouterFunction<?> routerFunction(){
		return RouterFunctions.route(GET("/reservations/names"), request -> {
			return ok().body(fromObject("Hi !"));
		}).andRoute(POST("/reservations"), request -> {
			reservationWriter.write("This is a test message");
		});
	}

	public Mono<ServerResponse> fallback(){
		return ok().body(fromObject(new ArrayList()));
	}
}

@MessagingGateway
interface ReservationWriter{
	@Gateway(requestChannel = "output")
	void write(String rn);
}

interface ReservationChannels {

	@Output
	MessageChannel output();
}

@FeignClient("reservation-service")
interface ReservationReader{

	@GetMapping("/reservations")
	Mono<ServerResponse> read();

}



