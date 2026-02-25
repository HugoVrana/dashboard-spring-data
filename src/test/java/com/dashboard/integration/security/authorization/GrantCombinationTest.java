package com.dashboard.integration.security.authorization;

import com.dashboard.dataTransferObject.invoice.InvoiceCreate;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Story("Grant Combinations")
@DisplayName("Grant Combination Tests")
public class GrantCombinationTest extends BaseAuthorizationSecurityTest {

    @Test
    @DisplayName("Multiple grants allow access to multiple endpoints")
    void multipleGrants_AllowMultipleEndpoints() throws Exception {
        String authHeader = authHeader("dashboard-invoices-read", "dashboard-customers-read");

        mockMvc.perform(get("/invoices/")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk());

        mockMvc.perform(get("/customers/")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("All invoice grants allow full invoice CRUD")
    void allInvoiceGrants_AllowFullCrud() throws Exception {
        String authHeader = authHeader(
                "dashboard-invoices-read",
                "dashboard-invoices-create",
                "dashboard-invoices-update",
                "dashboard-invoices-delete"
        );

        mockMvc.perform(get("/invoices/")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk());

        InvoiceCreate invoiceCreate = new InvoiceCreate("pending", new BigDecimal("500.00"), testCustomer.get_id().toHexString());
        mockMvc.perform(post("/invoices")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceCreate)))
                .andExpect(status().isCreated());
    }
}
