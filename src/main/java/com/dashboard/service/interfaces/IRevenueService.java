package com.dashboard.service.interfaces;

import com.dashboard.model.entities.Revenue;

import java.time.Month;
import java.util.List;

public interface IRevenueService {
    List<Revenue> getAllRevenues();
    void adjustRevenue(Month month, Integer year, Double delta);
}
