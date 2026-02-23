package com.dashboard.service;

import com.dashboard.model.entities.Revenue;
import com.dashboard.repository.IRevenueRepository;
import com.dashboard.service.interfaces.IRevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.Year;
import java.util.List;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;

@Service
@Scope("singleton")
@RequiredArgsConstructor
public class RevenueService implements IRevenueService {

    private final IRevenueRepository revenueRepository;
    private final MongoTemplate mongoTemplate;

    public List<Revenue> getAllRevenues() {
        return revenueRepository.queryByAudit_DeletedAtIsNull();
    }

    public void adjustRevenue(Month month, Year year, Double delta) {
        Revenue r = revenueRepository.findRevenueByMonthAndYear(month, year);
        r.setRevenue(r.getRevenue() + delta);
        revenueRepository.save(r);
    }
}
