package com.example.springboot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
//@RequestMapping("/api/")
public class HelloController {

	@GetMapping({ "/" })
	public String index() {
		log.info("Root api endpoint is accessed");
		return "Greetings from Spring Boot!";
	}

}
