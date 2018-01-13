package com.example.reservationclient;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@EnableFeignClients
@EnableZuulProxy
@EnableCircuitBreaker
@EnableDiscoveryClient
@SpringBootApplication
public class ReservationClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationClientApplication.class, args);
    }
}

@FeignClient("reservation-service")
interface ReservationReader{

    @GetMapping("/reservations")
    Collection<Reservation>read();
}

@Data
class Reservation{
    private String reservationName;
    private Long id;
}

@RestController
@RequestMapping("/reservations")
class ResservationApiAdapterRestController {

    private final ReservationReader reservationReader;

    ResservationApiAdapterRestController (ReservationReader reservationReader){
        this.reservationReader = reservationReader;
    }

    public Collection<String>fallback(){
        return new ArrayList<>();
    }

    @HystrixCommand(fallbackMethod = "fallback")
    @GetMapping("/names")
    public Collection<String> names() {
        return this.reservationReader.read()
                .stream()
                .map(Reservation::getReservationName)
                .collect(Collectors.toList());
    }
}