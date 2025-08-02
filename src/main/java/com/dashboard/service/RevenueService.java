package com.dashboard.service;

import com.dashboard.repository.IRevenueRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class RevenueService {
    private final IRevenueRepository revenueRepository;

    public RevenueService(IRevenueRepository revenueRepository) {
        this.revenueRepository = revenueRepository;
    }
}
