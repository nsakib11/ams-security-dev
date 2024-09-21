package com.ams.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class AmsSecurity {

	@GetMapping("/test")
	public String home() {
		return "My first aws spring boot application deployed successfully";
	}

	public static void main(String[] args) {
		SpringApplication.run(AmsSecurity.class, args);
	}

}
