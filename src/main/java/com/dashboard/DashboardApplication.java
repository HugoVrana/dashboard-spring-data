package com.dashboard;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
public class DashboardApplication {

	@GetMapping("/")
	public String home() {
		return "Spring Dashboard is here!";
	}

	public static void main(String[] args) {
		SpringApplication.run(DashboardApplication.class, args);
	}
}
