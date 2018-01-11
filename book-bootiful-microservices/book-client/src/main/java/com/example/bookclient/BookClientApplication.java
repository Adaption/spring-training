package com.example.bookclient;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@EnableCircuitBreaker
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
	@RequestMapping(method = RequestMethod.GET, value = "books")
	Resources<Book> read();
}

@Data
@AllArgsConstructor
class Book{
	private String name;
	private String author;
}

@RestController
@RequestMapping("/books")
class BookApiGateWay{

	private BookReader bookReader;

	@Autowired
	public BookApiGateWay(BookReader bookReader) {
		this.bookReader = bookReader;
	}

	public Collection<String> fallback(){
		return new ArrayList<>();
	}

	@HystrixCommand(fallbackMethod = "fallback")
	@RequestMapping(method = RequestMethod.GET,value = "/names")
	public Collection<String> names(){
		return this.bookReader.read().getContent().stream().map(b -> b.getName()).collect(Collectors.toList());
	}
}