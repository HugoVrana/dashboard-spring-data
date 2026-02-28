package com.dashboard.controller;

import com.dashboard.dataTransferObject.revenue.RevenueRead;
import com.dashboard.mapper.interfaces.IRevenueMapper;
import com.dashboard.service.interfaces.IRevenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@CrossOrigin
@Tag(name = "Revenues", description = "Revenue data operations")
@RequestMapping(value = "/revenues", produces = "application/json")
@RequiredArgsConstructor
public class RevenuesController {

    private final IRevenueService revenueService;
    private final IRevenueMapper revenueMapper;

    @Operation(summary = "Get all revenues", description = "Retrieves monthly revenue data")
    @GetMapping("/")
    @PreAuthorize("hasAuthority('dashboard-revenue-read')")
    public ResponseEntity<List<RevenueRead>> getAllRevenues() {
        List<RevenueRead> revenueReads = revenueService.getAllRevenues().stream()
                .map(revenueMapper::toRead)
                .toList();
        return ResponseEntity.ok(revenueReads);
    }
}