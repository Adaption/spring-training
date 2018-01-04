package com.example.bookclient;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@EnableFeignClients
@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
public class BookClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookClientApplication.class, args);
	}
}

@FeignClient("book-service")
interface BookReader{

	@GetMapping("/books")
	Collection<Book> read();
}

@Data
class Book{
	private Long id;
	private String name;
	private String author;
}

@RestController
@RequestMapping("/books")
class BookAdapterApiRestController{

	private final BookReader bookReader;

	public BookAdapterApiRestController(BookReader bookReader) {
		this.bookReader = bookReader;
	}

	public Collection<String> fallback(){
		return new ArrayList<>();
	}

	@HystrixCommand(fallbackMethod = "fallback")
	@GetMapping("/names")
	Collection<String> names(){
		return this.bookReader.read().stream().map(Book::getName).collect(Collectors.toList());
	}
}