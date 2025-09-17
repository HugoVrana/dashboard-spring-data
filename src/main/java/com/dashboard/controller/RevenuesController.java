package com.dashboard.controller;

import com.dashboard.dataTransferObject.revenue.RevenueRead;
import com.dashboard.mapper.interfaces.IRevenueMapper;
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
    private final IRevenueMapper revenueMapper;

    public RevenuesController(IRevenueService revenueService, IRevenueMapper revenueMapper) {
        this.revenueService = revenueService;
        this.revenueMapper = revenueMapper;
    }

    @GetMapping("/")
    public List<RevenueRead> getAllRevenues() {
        List<Revenue> revenues = revenueService.getAllRevenues();
        List<RevenueRead> revenueDtos = new java.util.ArrayList<>();
        for(Revenue revenue : revenues) {
            RevenueRead revenueRead = revenueMapper.toRead(revenue);
            revenueDtos.add(revenueRead);
        }
        return revenueDtos;
    }
}