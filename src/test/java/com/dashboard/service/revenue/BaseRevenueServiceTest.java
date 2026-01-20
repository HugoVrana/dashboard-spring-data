package com.dashboard.service.revenue;

import com.dashboard.common.model.Audit;
import com.dashboard.model.entities.Revenue;
import com.dashboard.repository.IRevenueRepository;
import com.dashboard.service.RevenueService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ExtendWith(MockitoExtension.class)
public abstract class BaseRevenueServiceTest {

    @MockitoBean
    protected IRevenueRepository revenueRepository;

    protected Revenue testRevenue;

    @MockitoBean
    protected RevenueService revenueService;

    @BeforeEach
    void setUp() {
        testRevenue = new Revenue();
        testRevenue.set_id(new ObjectId());
        testRevenue.setMonth("January");
        testRevenue.setRevenue(10000.0);
        testRevenue.setAudit(new Audit());
    }
}
