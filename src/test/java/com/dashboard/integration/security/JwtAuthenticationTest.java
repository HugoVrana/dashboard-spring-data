package com.dashboard.integration.security;

import com.dashboard.config.TestJwtTokenGenerator;
import com.dashboard.integration.BaseIntegrationTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for JWT authentication filter behavior.
 */
@Feature("JWT Authentication")
@Tag("security")
@DisplayName("JWT Authentication Tests")
public class JwtAuthenticationTest extends BaseIntegrationTest {

    @Test
    @Story("Valid Token")
    @DisplayName("should succeed with valid token")
    void validToken_Succeeds() throws Exception {
        mockMvc.perform(get("/invoices/")
                        .header("Authorization", authHeader("dashboard-invoices-read")))
                .andExpect(status().isOk());
    }

    @Test
    @Story("Missing Token")
    @DisplayName("should return 403 when token is missing")
    void missingToken_Returns403() throws Exception {
        mockMvc.perform(get("/invoices/"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Story("Expired Token")
    @DisplayName("should return 403 when token is expired")
    void expiredToken_Returns403() throws Exception {
        String expiredToken = TestJwtTokenGenerator.generateExpiredToken();

        mockMvc.perform(get("/invoices/")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @Story("Invalid Signature")
    @DisplayName("should return 403 when token has invalid signature")
    void invalidSignature_Returns403() throws Exception {
        String invalidToken = TestJwtTokenGenerator.generateTokenWithInvalidSignature();

        mockMvc.perform(get("/invoices/")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @Story("Malformed Token")
    @DisplayName("should return 403 when token is malformed")
    void malformedToken_Returns403() throws Exception {
        mockMvc.perform(get("/invoices/")
                        .header("Authorization", "Bearer not.a.valid.jwt.token"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Story("Invalid Authorization Header")
    @DisplayName("should return 403 when authorization header has wrong format")
    void wrongAuthFormat_Returns403() throws Exception {
        mockMvc.perform(get("/invoices/")
                        .header("Authorization", "Basic sometoken"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Story("Empty Bearer Token")
    @DisplayName("should return 403 when bearer token is empty")
    void emptyBearerToken_Returns403() throws Exception {
        mockMvc.perform(get("/invoices/")
                        .header("Authorization", "Bearer "))
                .andExpect(status().isForbidden());
    }
}
