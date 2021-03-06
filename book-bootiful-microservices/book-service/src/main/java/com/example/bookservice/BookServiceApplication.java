package com.example.bookservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.List;

@EnableBinding(BookChannels.class)
@EnableDiscoveryClient
@SpringBootApplication
public class BookServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookServiceApplication.class, args);
    }
}

interface BookChannels {
    @Input("createBook")
    SubscribableChannel createBook();
}

@MessageEndpoint
class BookProcessor {

    @ServiceActivator(inputChannel = "createBook")
    public void onNewBook(String bookName) {
        this.bookRepository.save(new Book(null, bookName, null));
    }

    private final BookRepository bookRepository;

    @Autowired
    public BookProcessor(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
}

@RestController
@RefreshScope
class MessageRestController {
    private final String value;

    @Autowired
    public MessageRestController(@Value("${message}") String value) {
        this.value = value;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/message")
    String read() {
        return this.value;
    }
}

@Component
class DataInitializer implements CommandLineRunner {
    BookRepository bookRepository;

    public DataInitializer(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... strings) throws Exception {
        List<Book> bookList = Arrays.asList(
                new Book(null, "Dark Souls", "Software"),
                new Book(null, "Dark LKady", "Michael")
        );
        bookList.forEach(book -> bookRepository.save(book));

        bookRepository.findAll().forEach(System.out::println);
    }
}

@RepositoryRestResource
interface BookRepository extends JpaRepository<Book, Long> {
}

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
class Book {

    @Id
    @GeneratedValue
    private Long Id;

    private String bookName;

    private String bookAuthor;
}
