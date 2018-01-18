package com.example.bookservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@EnableDiscoveryClient
@SpringBootApplication
public class BookServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookServiceApplication.class, args);
	}
}

@RestController
class BookApiResetController{

	private final BookRepository bookRepository;


	public BookApiResetController(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	@GetMapping("/books")
	Collection<Book> books(){
		return this.bookRepository.findAll();
	}
}

@Component
class DataInitializer implements ApplicationRunner{

	BookRepository bookRepository;

	public DataInitializer(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
		List<Book> books = Arrays.asList(
				new Book(null,"Dark Soul","Software"),
				new Book(null,"A Duck","An Egg"),
				new Book(null,"COD","EA"),
				new Book(null,"AWDS","tiamak")
				);

		books.forEach(book -> bookRepository.save(book));
		bookRepository.findAll().forEach(System.out::println);
	}
}

interface BookRepository extends JpaRepository<Book,Long>{

}

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
class Book{
	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private String author;
}
