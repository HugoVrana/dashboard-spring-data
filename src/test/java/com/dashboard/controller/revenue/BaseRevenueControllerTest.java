package com.dashboard.controller.revenue;

import com.dashboard.common.logging.GrafanaHttpClient;
import com.dashboard.common.model.Audit;
import com.dashboard.controller.RevenuesController;
import com.dashboard.dataTransferObject.revenue.RevenueRead;
import com.dashboard.mapper.interfaces.IRevenueMapper;
import com.dashboard.model.entities.Revenue;
import com.dashboard.service.interfaces.IRevenueService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import net.datafaker.Faker;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.dashboard.config.TestConfig;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;

@Epic("Revenue")
@Feature("Revenue API")
@Tag("controller-revenue")
@WebMvcTest(RevenuesController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestConfig.class)
@Execution(ExecutionMode.SAME_THREAD)
@ResourceLock("spring-context")
@WithMockUser(username = "testUser")
public abstract class BaseRevenueControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected IRevenueService revenueService;

    @MockitoBean
    protected IRevenueMapper revenueMapper;

    @MockitoBean
    protected GrafanaHttpClient grafanaHttpClient;

    protected final Faker faker = new Faker();

    protected ObjectId testRevenueId;
    protected Revenue testRevenue;
    protected RevenueRead testRevenueRead;

    protected Month testMonth;
    protected Integer testYear;
    protected BigDecimal testRevenueAmount;

    @BeforeEach
    void setUpBase() {
        testRevenueId = new ObjectId();
        testMonth = Month.of(faker.number().numberBetween(1, 12));
        testYear = 2024;
        testRevenueAmount = BigDecimal.valueOf(faker.number().randomDouble(2, 1000, 50000))
                .setScale(2, RoundingMode.HALF_UP);

        testRevenue = new Revenue();
        testRevenue.set_id(testRevenueId);
        testRevenue.setMonth(testMonth);
        testRevenue.setYear(testYear);
        testRevenue.setRevenue(testRevenueAmount);
        testRevenue.setAudit(new Audit());

        testRevenueRead = new RevenueRead();
        testRevenueRead.setId(testRevenueId.toHexString());
        testRevenueRead.setMonth(testMonth.name());
        testRevenueRead.setRevenue(testRevenueAmount);
    }
}
