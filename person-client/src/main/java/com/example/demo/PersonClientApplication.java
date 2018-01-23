package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import feign.Request;
import feign.RequestInterceptor;

@SpringBootApplication
@EnableDiscoveryClient
@EnableOAuth2Sso
@EnableZuulProxy
@EnableFeignClients
public class PersonClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonClientApplication.class, args);
	}
}

@FeignClient(name = "person-service", configuration = FeignClientConfiguration.class)
interface PersonReader {

	@GetMapping("/")
	List<Person> read();

	@PostMapping("/")
	Person addPerson(@RequestBody Person person);

}

@Configuration
class FeignClientConfiguration {

	@Value("${security.oauth2.client.user-authorization-uri}")
	private String authorizeUrl;

	@Value("${security.oauth2.client.access-token-uri}")
	private String tokenUrl;

	@Value("${security.oauth2.client.client-id}")
	private String clientId;
	@Value("${security.oauth2.client.client-secret}")
	private String secret;

	@Bean
	public RequestInterceptor oauth2FeignRequestInterceptor(OAuth2ClientContext oauth2ClientContext) {
		return new OAuth2FeignRequestInterceptor(oauth2ClientContext, resource());
	}

	@Bean
	protected OAuth2ProtectedResourceDetails resource() {
		AuthorizationCodeResourceDetails resource = new AuthorizationCodeResourceDetails();
		resource.setAccessTokenUri(tokenUrl);
		resource.setUserAuthorizationUri(authorizeUrl);
		resource.setClientId(clientId);
		resource.setClientSecret(secret);
		return resource;
	}

	@Bean
	public Request.Options options() {
		return new Request.Options(10000, 10000);
	}
}

@Controller
class RController {

	private final PersonReader reader;

	public RController(PersonReader reader) {
		this.reader = reader;
	}

	@GetMapping("/")
	public String getPerson(Model model) {
		model.addAttribute("person", new Person());
		model.addAttribute("persons", this.reader.read());
		return "index";
	}

	@PostMapping("/")
	public String addPerson(Person person) {
		this.reader.addPerson(person);
		return "redirect:/";
	}
}

class Person {

	private Long id;

	private String name;

	private String email;

	public Person() {
	}

	public Person(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
