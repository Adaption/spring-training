package com.example.bookservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class BookServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookServiceApplication.class, args);
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
                new Book(null,"Dark Souls","Software"),
                new Book(null,"Dark LKady","Michael")
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

    @javax.persistence.Id
    @GeneratedValue
    private Long Id;

    private String name;

    private String author;
}
