package com.dashboard.service.interfaces;

import com.dashboard.model.entities.Revenue;

import java.time.Month;
import java.time.Year;
import java.util.List;

public interface IRevenueService {
    List<Revenue> getAllRevenues();
    void adjustRevenue(Month month, Year year, Double delta);
}
