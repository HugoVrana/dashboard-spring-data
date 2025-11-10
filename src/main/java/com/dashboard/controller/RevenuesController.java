package com.dashboard.controller;

import com.dashboard.dataTransferObject.revenue.RevenueRead;
import com.dashboard.mapper.interfaces.IRevenueMapper;
import com.dashboard.model.entities.Revenue;
import com.dashboard.service.interfaces.IRevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/revenues")
@RequiredArgsConstructor
public class RevenuesController {

    private final IRevenueService revenueService;
    private final IRevenueMapper revenueMapper;

    @GetMapping("/")
    public ResponseEntity<List<RevenueRead>> getAllRevenues() {
        List<Revenue> revenues = revenueService.getAllRevenues();
        List<RevenueRead> revenueReads = new ArrayList<>();
        for(Revenue revenue : revenues) {
            RevenueRead revenueRead = revenueMapper.toRead(revenue);
            revenueReads.add(revenueRead);
        }
        return ResponseEntity.ok(revenueReads);
    }
}