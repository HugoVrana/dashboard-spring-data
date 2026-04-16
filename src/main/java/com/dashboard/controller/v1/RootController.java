package com.dashboard.controller.v1;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Void> redirectToSwagger() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "/swagger-ui/index.html");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
