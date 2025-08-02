package com.dashboard.controller;

import com.dashboard.service.RevenueService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/revenues")
public class RevenuesController {
    private final RevenueService revenueService;

    public RevenuesController(RevenueService revenueService) {
        this.revenueService = revenueService;
    }
}
