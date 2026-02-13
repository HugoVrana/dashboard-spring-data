package com.dashboard.integration.security.authorization;

import com.dashboard.dataTransferObject.invoice.InvoiceCreate;
import com.dashboard.dataTransferObject.invoice.InvoiceUpdate;
import com.dashboard.dataTransferObject.page.PageRequest;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Story("Invoice Endpoints")
@DisplayName("Invoice Authorization")
public class InvoiceAuthorizationTest extends BaseAuthorizationSecurityTest {

    @Test
    @DisplayName("GET /invoices/ - correct grant allows access")
    void getAllInvoices_WithCorrectGrant_Returns200() throws Exception {
        mockMvc.perform(get("/invoices/")
                        .header("Authorization", authHeader("dashboard-invoices-read")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /invoices/ - missing grant returns 403")
    void getAllInvoices_WithoutGrant_Returns403() throws Exception {
        mockMvc.perform(get("/invoices/")
                        .header("Authorization", authHeader("dashboard-customers-read")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /invoices/{id} - correct grant allows access")
    void getInvoiceById_WithCorrectGrant_Returns200() throws Exception {
        mockMvc.perform(get("/invoices/" + testInvoice.get_id().toHexString())
                        .header("Authorization", authHeader("dashboard-invoices-read")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /invoices/{id} - missing grant returns 403")
    void getInvoiceById_WithoutGrant_Returns403() throws Exception {
        mockMvc.perform(get("/invoices/" + testInvoice.get_id().toHexString())
                        .header("Authorization", authHeader("dashboard-users-read")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /invoices - correct grant allows access")
    void createInvoice_WithCorrectGrant_Returns201() throws Exception {
        InvoiceCreate invoiceCreate = new InvoiceCreate("pending", 500.0, testCustomer.get_id().toHexString());

        mockMvc.perform(post("/invoices")
                        .header("Authorization", authHeader("dashboard-invoices-create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceCreate)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /invoices - missing grant returns 403")
    void createInvoice_WithoutGrant_Returns403() throws Exception {
        InvoiceCreate invoiceCreate = new InvoiceCreate("pending", 500.0, testCustomer.get_id().toHexString());

        mockMvc.perform(post("/invoices")
                        .header("Authorization", authHeader("dashboard-invoices-read"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceCreate)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /invoices/{id} - correct grant allows access")
    void updateInvoice_WithCorrectGrant_Returns201() throws Exception {
        InvoiceUpdate invoiceUpdate = new InvoiceUpdate(
                testInvoice.get_id().toHexString(),
                "paid",
                1500.0,
                testCustomer.get_id().toHexString()
        );

        mockMvc.perform(put("/invoices/" + testInvoice.get_id().toHexString())
                        .header("Authorization", authHeader("dashboard-invoices-update"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceUpdate)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("PUT /invoices/{id} - missing grant returns 403")
    void updateInvoice_WithoutGrant_Returns403() throws Exception {
        InvoiceUpdate invoiceUpdate = new InvoiceUpdate(
                testInvoice.get_id().toHexString(),
                "paid",
                1500.0,
                testCustomer.get_id().toHexString()
        );

        mockMvc.perform(put("/invoices/" + testInvoice.get_id().toHexString())
                        .header("Authorization", authHeader("dashboard-invoices-read"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceUpdate)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /invoices/{id} - correct grant allows access")
    void deleteInvoice_WithCorrectGrant_Returns200() throws Exception {
        mockMvc.perform(delete("/invoices/" + testInvoice.get_id().toHexString())
                        .header("Authorization", authHeader("dashboard-invoices-delete")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /invoices/{id} - missing grant returns 403")
    void deleteInvoice_WithoutGrant_Returns403() throws Exception {
        mockMvc.perform(delete("/invoices/" + testInvoice.get_id().toHexString())
                        .header("Authorization", authHeader("dashboard-invoices-read")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /invoices/search - correct grant allows access")
    void searchInvoices_WithCorrectGrant_Returns204() throws Exception {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setSearch("");
        pageRequest.setPage(1);
        pageRequest.setSize(10);

        mockMvc.perform(post("/invoices/search")
                        .header("Authorization", authHeader("dashboard-invoices-read"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /invoices/search - missing grant returns 403")
    void searchInvoices_WithoutGrant_Returns403() throws Exception {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setSearch("");
        pageRequest.setPage(1);
        pageRequest.setSize(10);

        mockMvc.perform(post("/invoices/search")
                        .header("Authorization", authHeader("dashboard-customers-read"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isForbidden());
    }
}
