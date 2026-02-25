package com.dashboard.service;

import com.dashboard.model.entities.Revenue;
import com.dashboard.repository.IRevenueRepository;
import com.dashboard.service.interfaces.IRevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Month;
import java.util.List;

@Service
@Scope("singleton")
@RequiredArgsConstructor
public class RevenueService implements IRevenueService {

    private final IRevenueRepository revenueRepository;
    private final MongoTemplate mongoTemplate;

    public List<Revenue> getAllRevenues() {
        return revenueRepository.queryByAudit_DeletedAtIsNull();
    }

    public void adjustRevenue(Month month, Integer year, BigDecimal delta) {
        try {
            List<Revenue> revenueList = revenueRepository.getRevenueByYearIsAndMonthIs(year, month);
            if (revenueList.isEmpty()) {
                Revenue revenue = new Revenue();
                revenue.setMonth(month);
                revenue.setYear(year);
                revenue.setRevenue(delta);
                revenueRepository.save(revenue);
            }

            for (Revenue revenue : revenueList) {
                revenue.setRevenue(revenue.getRevenue().add(delta));
                revenueRepository.save(revenue);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
