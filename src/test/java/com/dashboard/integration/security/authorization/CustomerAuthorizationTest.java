package com.dashboard.integration.security.authorization;

import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Story("Customer Endpoints")
@DisplayName("Customer Authorization")
public class CustomerAuthorizationTest extends BaseAuthorizationSecurityTest {

    @Test
    @DisplayName("GET /customers/ - correct grant allows access")
    void getAllCustomers_WithCorrectGrant_Returns200() throws Exception {
        mockMvc.perform(get("/customers/")
                        .header("Authorization", authHeader("dashboard-customers-read")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /customers/ - missing grant returns 403")
    void getAllCustomers_WithoutGrant_Returns403() throws Exception {
        mockMvc.perform(get("/customers/")
                        .header("Authorization", authHeader("dashboard-invoices-read")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /customers/{id} - correct grant allows access")
    void getCustomerById_WithCorrectGrant_Returns200() throws Exception {
        mockMvc.perform(get("/customers/" + testCustomer.get_id().toHexString())
                        .header("Authorization", authHeader("dashboard-customers-read")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /customers/count - correct grant allows access")
    void getCustomerCount_WithCorrectGrant_Returns200() throws Exception {
        mockMvc.perform(get("/customers/count")
                        .header("Authorization", authHeader("dashboard-customers-read")))
                .andExpect(status().isOk());
    }
}
