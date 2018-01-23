package com.example.demo;

import java.security.Principal;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@EnableResourceServer
public class PersonServiceApplication extends ResourceServerConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(PersonServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner command(PersonRepository repo) {
		return args -> {

			Person p = new Person("Jon", "jon@gmail.com");
			Person p1 = new Person("Dave", "dave@gmail.com");
			repo.save(p);
			repo.save(p1);

		};
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
		.csrf().disable()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.authorizeRequests()
		.antMatchers(HttpMethod.GET, "/**").access("hasRole('USER')")
		.antMatchers(HttpMethod.POST, "/**").access("hasRole('USER')");
	}

}

@RestController
class RController {

	private final PersonRepository personRepository;

	public RController(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	@GetMapping("/")
	public List<Person> getPeople(Principal principal){
		return personRepository.findAll();
	}

	@PostMapping("/")
	public Person addPerson(@RequestBody Person person){
		return personRepository.save(person);
	}
}

@Entity
class Person {

	@Id
	@GeneratedValue
	private Long id;

	private String name;
	
	private String email;

	public Person() {

	}

	public Person(String name, String email) {
		this.name = name;
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

interface PersonRepository extends JpaRepository<Person, Long> {

}