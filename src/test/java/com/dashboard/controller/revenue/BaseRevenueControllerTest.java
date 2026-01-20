package com.dashboard.controller.revenue;

import com.dashboard.common.logging.GrafanaHttpClient;
import com.dashboard.common.model.Audit;
import com.dashboard.controller.RevenuesController;
import com.dashboard.dataTransferObject.revenue.RevenueRead;
import com.dashboard.mapper.interfaces.IRevenueMapper;
import com.dashboard.model.entities.Revenue;
import com.dashboard.service.interfaces.IRevenueService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RevenuesController.class)
@AutoConfigureMockMvc(addFilters = false)
public abstract class BaseRevenueControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

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

    protected String testMonth;
    protected Double testRevenueAmount;

    @BeforeEach
    void setUpBase() {
        testRevenueId = new ObjectId();
        testMonth = faker.options().option("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        testRevenueAmount = faker.number().randomDouble(2, 1000, 50000);

        testRevenue = new Revenue();
        testRevenue.set_id(testRevenueId);
        testRevenue.setMonth(testMonth);
        testRevenue.setRevenue(testRevenueAmount);
        testRevenue.setAudit(new Audit());

        testRevenueRead = new RevenueRead();
        testRevenueRead.setId(testRevenueId.toHexString());
        testRevenueRead.setMonth(testMonth);
        testRevenueRead.setRevenue(testRevenueAmount);
    }
}
