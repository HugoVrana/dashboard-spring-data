package com.dashboard.integration.revenues;

import com.dashboard.integration.BaseIntegrationTest;
import com.dashboard.model.entities.Revenue;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * E2E integration tests for Revenue endpoints.
 */
@Feature("Revenue E2E")
@DisplayName("Revenue E2E Tests")
public class RevenueE2ETest extends BaseIntegrationTest {

    @Test
    @Story("Get All Revenues")
    @DisplayName("GET /revenues/ returns all active revenues")
    void getAllRevenues_ReturnsAllActiveRevenues() throws Exception {
        Revenue revenue1 = createAndSaveRevenue();
        Revenue revenue2 = createAndSaveRevenue();

        mockMvc.perform(get("/revenues/")
                        .header("Authorization", authHeader("dashboard-revenue-read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(
                        revenue1.get_id().toHexString(),
                        revenue2.get_id().toHexString()
                )));
    }

    @Test
    @Story("Soft Delete Exclusion")
    @DisplayName("GET /revenues/ excludes soft-deleted revenues")
    void getAllRevenues_ExcludesSoftDeleted() throws Exception {
        Revenue activeRevenue = createAndSaveRevenue();

        // Create and soft-delete a revenue
        Revenue deletedRevenue = createAndSaveRevenue();
        deletedRevenue.setAudit(createDeletedAudit());
        revenueRepository.save(deletedRevenue);

        mockMvc.perform(get("/revenues/")
                        .header("Authorization", authHeader("dashboard-revenue-read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(activeRevenue.get_id().toHexString()));
    }

    @Test
    @Story("Empty Results")
    @DisplayName("GET /revenues/ returns empty array when no revenues")
    void getAllRevenues_ReturnsEmptyWhenNoRevenues() throws Exception {
        mockMvc.perform(get("/revenues/")
                        .header("Authorization", authHeader("dashboard-revenue-read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Story("Revenue Data")
    @DisplayName("GET /revenues/ returns revenue with month and amount")
    void getAllRevenues_ReturnsRevenueWithData() throws Exception {
        Revenue revenue = createAndSaveRevenue();

        mockMvc.perform(get("/revenues/")
                        .header("Authorization", authHeader("dashboard-revenue-read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].month").value(revenue.getMonth()))
                .andExpect(jsonPath("$[0].revenue").value(revenue.getRevenue()));
    }
}
