package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/cities")
public class RestEndpoint {

    @GetMapping
    public List<Country> getCountries(){
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("United States of America", "Washington D.C.", 339_996_563));
        countries.add(new Country("China", "Beijing", 1_411_750_000));
        countries.add(new Country("India", "New Delhi", 1_428_627_663));
        return countries;
    }
}