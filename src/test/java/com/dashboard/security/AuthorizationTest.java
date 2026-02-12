package com.dashboard.security;

import com.dashboard.dataTransferObject.invoice.InvoiceCreate;
import com.dashboard.dataTransferObject.invoice.InvoiceUpdate;
import com.dashboard.dataTransferObject.page.PageRequest;
import com.dashboard.integration.BaseIntegrationTest;
import com.dashboard.model.entities.Customer;
import com.dashboard.model.entities.Invoice;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for @PreAuthorize enforcement across all endpoints.
 */
@Feature("Authorization")
@Tag("security")
@DisplayName("Authorization Tests")
public class AuthorizationTest extends BaseIntegrationTest {

    private Customer testCustomer;
    private Invoice testInvoice;

    @BeforeEach
    void setUpData() {
        testCustomer = createAndSaveCustomer();
        testInvoice = createAndSaveInvoice(testCustomer);
    }

    @Nested
    @Story("Invoice Endpoints")
    @DisplayName("Invoice Authorization")
    class InvoiceAuthorizationTests {

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

    @Nested
    @Story("Customer Endpoints")
    @DisplayName("Customer Authorization")
    class CustomerAuthorizationTests {

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

    @Nested
    @Story("User Endpoints")
    @DisplayName("User Authorization")
    class UserAuthorizationTests {

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

    @Nested
    @Story("Revenue Endpoints")
    @DisplayName("Revenue Authorization")
    class RevenueAuthorizationTests {

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

    @Nested
    @Story("Grant Combinations")
    @DisplayName("Grant Combination Tests")
    class GrantCombinationTests {

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

            InvoiceCreate invoiceCreate = new InvoiceCreate("pending", 500.0, testCustomer.get_id().toHexString());
            mockMvc.perform(post("/invoices")
                            .header("Authorization", authHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invoiceCreate)))
                    .andExpect(status().isCreated());
        }
    }
}
