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
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.hateoas.Resources;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.MessageChannel;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@EnableResourceServer
@EnableBinding(BookChannels.class)
@EnableCircuitBreaker
@EnableFeignClients
@IntegrationComponentScan
@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
public class BookClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookClientApplication.class, args);
    }
}


interface BookChannels {
    @Output("createBook")
    MessageChannel createBook();
}


@FeignClient("book-service")
interface BookReader {
    @RequestMapping(method = RequestMethod.GET, value = "books")
    Resources<Book> read();
}

@MessagingGateway
interface BookWriter {

    @Gateway(requestChannel = "createBook")
    void write(String rn);
}

@Data
@AllArgsConstructor
class Book {
    private String bookName;
    private String bookAuthor;
}

@RestController
@RequestMapping("/books")
class BookApiGateWay {

    private final BookReader bookReader;
    private final BookWriter bookWriter;

    @Autowired
    public BookApiGateWay(BookReader bookReader, BookWriter bookWriter) {
        this.bookReader = bookReader;
        this.bookWriter = bookWriter;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createBook(@RequestBody Book book) {
        this.bookWriter.write(book.getBookName());
    }

    public Collection<String> fallback() {
        return new ArrayList<>();
    }

    @HystrixCommand(fallbackMethod = "fallback")
    @RequestMapping(method = RequestMethod.GET, value = "/names")
    public Collection<String> names() {
        return this.bookReader.read().getContent().stream().map(b -> b.getBookName()).collect(Collectors.toList());
    }
}