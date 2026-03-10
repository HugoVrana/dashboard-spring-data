package com.dashboard.service;

import com.dashboard.common.utility.diff.DiffComparer;
import com.dashboard.common.utility.diff.DiffResult;
import com.dashboard.context.DiffContext;
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
                Revenue saved = revenueRepository.save(revenue);

                DiffComparer<Revenue> comparer = new DiffComparer<>(null, saved);
                DiffResult diff = comparer.compare();
                DiffContext.addDiff(diff.toJson());
                return;
            }

            for (Revenue revenue : revenueList) {
                Revenue oldState = new Revenue();
                oldState.set_id(revenue.get_id());
                oldState.setMonth(revenue.getMonth());
                oldState.setYear(revenue.getYear());
                oldState.setRevenue(revenue.getRevenue());

                revenue.setRevenue(revenue.getRevenue().add(delta));
                Revenue saved = revenueRepository.save(revenue);

                DiffComparer<Revenue> comparer = new DiffComparer<>(oldState, saved);
                DiffResult diff = comparer.compare();
                DiffContext.addDiff(diff.toJson());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
