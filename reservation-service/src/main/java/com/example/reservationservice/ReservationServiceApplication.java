package com.example.reservationservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;
import java.util.stream.Stream;

@SpringBootApplication
public class ReservationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class, args);
    }
}

@RestController
@RefreshScope
class MessageRestController {

    private final String value;

    MessageRestController(@Value("${message}") String value) {
        this.value = value;
    }

    @GetMapping("/message")
    String read() {
        return this.value;
    }
}

@RestController
class ReservationRestApiController {

    private final ReservationRepository reservationRepository;

    ReservationRestApiController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/reservation")
    Collection<Reservation> reservations() {
        return this.reservationRepository.findAll();
    }
}

@Component
class DataInitializer implements ApplicationRunner {

    private final ReservationRepository reservationRepository;

    DataInitializer(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        Stream.of("Nhat", "Phuong", "Khue", "Cuong", "Tuan",
                "Batman", "Superman", "Spiderman", "Ironman", "Aquaman")
                .forEach(name -> reservationRepository.save(new Reservation(null, name)));
        reservationRepository.findAll().forEach(System.out::println);
    }
}

interface ReservationRepository extends JpaRepository<Reservation, Long> {

}

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
class Reservation {

    @Id
    @GeneratedValue
    private Long id;

    private String reservationName;
}
