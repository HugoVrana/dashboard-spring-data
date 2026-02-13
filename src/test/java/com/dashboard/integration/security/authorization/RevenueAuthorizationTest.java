package com.dashboard.integration.security.authorization;

import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Story("Revenue Endpoints")
@DisplayName("Revenue Authorization")
public class RevenueAuthorizationTest extends BaseAuthorizationSecurityTest {
    @Test
    @DisplayName("GET /revenues/ - correct grant allows access")
    void getAllRevenues_WithCorrectGrant_Returns200() throws Exception {
        mockMvc.perform(get("/revenues/")
                        .header("Authorization", authHeader("dashboard-revenue-read")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /revenues/ - missing grant returns 403")
    void getAllRevenues_WithoutGrant_Returns403() throws Exception {
        mockMvc.perform(get("/revenues/")
                        .header("Authorization", authHeader("dashboard-invoices-read")))
                .andExpect(status().isForbidden());
    }
}
