package com.dashboard.controller.revenue;

import com.dashboard.common.model.Audit;
import com.dashboard.dataTransferObject.revenue.RevenueRead;
import com.dashboard.model.entities.Revenue;
import io.qameta.allure.Story;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Story("Get All Revenues")
@DisplayName("GET /revenues")
public class GetAllRevenuesTest extends BaseRevenueControllerTest {
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
                .andExpect(jsonPath("$[0].month").value(testMonth.name()))
                .andExpect(jsonPath("$[0].revenue").value(testRevenueAmount));
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
        secondRevenue.setMonth(Month.FEBRUARY);
        secondRevenue.setYear(2024);
        secondRevenue.setRevenue(6500.00);
        secondRevenue.setAudit(new Audit());

        RevenueRead secondRevenueRead = new RevenueRead();
        secondRevenueRead.setId(secondRevenueId.toHexString());
        secondRevenueRead.setMonth(Month.FEBRUARY.name());
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
                .andExpect(jsonPath("$[0].month").value(testMonth.name()))
                .andExpect(jsonPath("$[0].revenue").value(testRevenueAmount))
                .andExpect(jsonPath("$[1].month").value("FEBRUARY"))
                .andExpect(jsonPath("$[1].revenue").value(6500.00));
    }
}
