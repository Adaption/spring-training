# spring-training
Training for Building Microservices using Spring
# Services - Dependencies
- book-service - Web, H2, Config Client, Eureka Discovery, Zipkin Client, Stream Rabbit, Rest Repositories, JPA, Actuator, Lombok
- config-server - Config Server
	-> the config is at https://github.com/swomfire/book-bootiful-microservices-config
- eureka-service - Eureka Server, Config Client
- book-client - Feign, Config Client, Eureka Discovery,Rest Repositories, Stream Rabbit, Zipkin Client, Hystrix, Zuul, Cloud OAuth2, HATEOAS, Web, Actuator, Lombok
- hystrix-dashboard - Hystrix Dashboard, Config Client, Eureka Discovery
- zipkin-service - Zipkin UI, Zipkin Server, Eureka Discovery
- auth-service - Cloud OAuth2, Eureka Discovery, Config Client, H2, JPA, Web