package reservationclient.reservationclient;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@EnableFeignClients
@EnableZuulProxy
@EnableCircuitBreaker
@EnableDiscoveryClient
@SpringBootApplication
public class ReservationClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationClientApplication.class, args);
	}

	@HystrixCommand(fallbackMethod = "fallback")
	@Bean
	RouterFunction<?> routerFunction(){
		return RouterFunctions.route(RequestPredicates.GET("/reservations/names"), request -> {
			return ServerResponse.ok().body(BodyInserters.fromObject("Hi !"));
		});
	}

	public Mono<ServerResponse> fallback(){
		return ServerResponse.ok().body(BodyInserters.fromObject(new ArrayList()));
	}
}

@FeignClient("reservation-service")
interface ReservationReader{

	@GetMapping("/reservations")
	Mono<ServerResponse> read();

}



