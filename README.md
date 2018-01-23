# oauth
Spring Boot Oauth Feign example

This is a demo using Spring Boot and various Spring Cloud projects to show how to use oauth, specifically the Authorization Code Grant. It uses Google as an Authorization server. It is also using Feign as a client to connect to the downstream services. To run the demo, first start the Eureka Server, then start the Person-Service, then start the Person-Client.

Go to localhost:8080 where you will be redirected to login so you can gain access to the person-service.

You can import the program into your IDE or you can start each individual service using <code>mvn clean package spring-boot:run</code> from the terminal in the respective directory.
