package com.dashboard.integration.security.authorization;

import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Story("User Endpoints")
@DisplayName("User Authorization")
public class UserAuthorizationTest extends BaseAuthorizationSecurityTest {
    @Test
    @DisplayName("GET /users/ - correct grant allows access")
    void getAllUsers_WithCorrectGrant_Returns200() throws Exception {
        mockMvc.perform(get("/users/")
                        .header("Authorization", authHeader("dashboard-users-read")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /users/ - missing grant returns 403")
    void getAllUsers_WithoutGrant_Returns403() throws Exception {
        mockMvc.perform(get("/users/")
                        .header("Authorization", authHeader("dashboard-invoices-read")))
                .andExpect(status().isForbidden());
    }
}
