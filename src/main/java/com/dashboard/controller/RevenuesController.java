package com.dashboard.controller;

import com.dashboard.model.Revenue;
import com.dashboard.service.interfaces.IRevenueService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/revenues")
public class RevenuesController {

    private final IRevenueService revenueService;

    public RevenuesController(IRevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @GetMapping("/")
    public List<Revenue> getAllRevenues() {
        return revenueService.getAllRevenues();
    }
}