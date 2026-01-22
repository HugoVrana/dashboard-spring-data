package com.dashboard.service.revenue;

import com.dashboard.common.model.Audit;
import com.dashboard.model.entities.Revenue;
import com.dashboard.repository.IRevenueRepository;
import com.dashboard.service.RevenueService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Epic("Revenue")
@Feature("Revenue Service")
@Tag("service-revenue")
@ExtendWith(MockitoExtension.class)
public abstract class BaseRevenueServiceTest {

    @Mock
    protected IRevenueRepository revenueRepository;

    protected Revenue testRevenue;

    @InjectMocks
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
