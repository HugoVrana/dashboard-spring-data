package com.dashboard.controller.revenue;

import com.dashboard.common.logging.GrafanaHttpClient;
import com.dashboard.common.model.Audit;
import com.dashboard.controller.RevenuesController;
import com.dashboard.dataTransferObject.revenue.RevenueRead;
import com.dashboard.mapper.interfaces.IRevenueMapper;
import com.dashboard.model.entities.Revenue;
import com.dashboard.service.interfaces.IRevenueService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RevenuesController.class)
@AutoConfigureMockMvc(addFilters = false)
class RevenuesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IRevenueService revenueService;

    @MockitoBean
    private IRevenueMapper revenueMapper;

    @MockitoBean
    private GrafanaHttpClient grafanaHttpClient;

    private Revenue testRevenue;
    private RevenueRead testRevenueRead;
    private ObjectId testRevenueId;

    @BeforeEach
    void setUp() {
        testRevenueId = new ObjectId();

        testRevenue = new Revenue();
        testRevenue.set_id(testRevenueId);
        testRevenue.setMonth("Jan");
        testRevenue.setRevenue(5000.00);
        testRevenue.setAudit(new Audit());

        testRevenueRead = new RevenueRead();
        testRevenueRead.setId(testRevenueId.toHexString());
        testRevenueRead.setMonth("Jan");
        testRevenueRead.setRevenue(5000.00);
    }

    @Nested
    @DisplayName("GET /revenues/")
    class GetAllRevenuesTests {

        @Test
        @DisplayName("should return all revenues")
        void getAllRevenues_ReturnsAllRevenues() throws Exception {
            List<Revenue> revenues = List.of(testRevenue);
            when(revenueService.getAllRevenues()).thenReturn(revenues);
            when(revenueMapper.toRead(testRevenue)).thenReturn(testRevenueRead);

            mockMvc.perform(get("/revenues/"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(testRevenueId.toHexString()))
                    .andExpect(jsonPath("$[0].month").value("Jan"))
                    .andExpect(jsonPath("$[0].revenue").value(5000.00));
        }

        @Test
        @DisplayName("should return empty list when no revenues exist")
        void getAllRevenues_ReturnsEmptyListWhenNoRevenues() throws Exception {
            when(revenueService.getAllRevenues()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/revenues/"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("should return multiple revenues")
        void getAllRevenues_ReturnsMultipleRevenues() throws Exception {
            Revenue secondRevenue = new Revenue();
            ObjectId secondRevenueId = new ObjectId();
            secondRevenue.set_id(secondRevenueId);
            secondRevenue.setMonth("Feb");
            secondRevenue.setRevenue(6500.00);
            secondRevenue.setAudit(new Audit());

            RevenueRead secondRevenueRead = new RevenueRead();
            secondRevenueRead.setId(secondRevenueId.toHexString());
            secondRevenueRead.setMonth("Feb");
            secondRevenueRead.setRevenue(6500.00);

            List<Revenue> revenues = List.of(testRevenue, secondRevenue);
            when(revenueService.getAllRevenues()).thenReturn(revenues);
            when(revenueMapper.toRead(testRevenue)).thenReturn(testRevenueRead);
            when(revenueMapper.toRead(secondRevenue)).thenReturn(secondRevenueRead);

            mockMvc.perform(get("/revenues/"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].month").value("Jan"))
                    .andExpect(jsonPath("$[0].revenue").value(5000.00))
                    .andExpect(jsonPath("$[1].month").value("Feb"))
                    .andExpect(jsonPath("$[1].revenue").value(6500.00));
        }
    }
}
