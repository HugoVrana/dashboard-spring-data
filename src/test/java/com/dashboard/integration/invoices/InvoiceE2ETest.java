package com.dashboard.integration.invoices;

import com.dashboard.dataTransferObject.invoice.InvoiceCreate;
import com.dashboard.dataTransferObject.invoice.InvoiceUpdate;
import com.dashboard.integration.BaseIntegrationTest;
import com.dashboard.model.entities.Customer;
import com.dashboard.model.entities.Invoice;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * E2E integration tests for Invoice endpoints.
 */
@Feature("Invoice E2E")
@DisplayName("Invoice E2E Tests")
public class InvoiceE2ETest extends BaseIntegrationTest {

    private Customer testCustomer;

    @BeforeEach
    void setUpData() {
        testCustomer = createAndSaveCustomer();
    }

    @Test
    @Story("Get All Invoices")
    @DisplayName("GET /invoices/ returns data from database")
    void getAllInvoices_ReturnsDataFromDatabase() throws Exception {
        Invoice invoice1 = createAndSaveInvoice(testCustomer);
        Invoice invoice2 = createAndSaveInvoice(testCustomer);

        mockMvc.perform(get("/invoices/")
                        .header("Authorization", authHeader("dashboard-invoices-read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(
                        invoice1.get_id().toHexString(),
                        invoice2.get_id().toHexString()
                )));
    }

    @Test
    @Story("Get Invoice By ID")
    @DisplayName("GET /invoices/{id} retrieves correct invoice")
    void getInvoiceById_RetrievesCorrectInvoice() throws Exception {
        Invoice invoice = createAndSaveInvoice(testCustomer);

        mockMvc.perform(get("/invoices/" + invoice.get_id().toHexString())
                        .header("Authorization", authHeader("dashboard-invoices-read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(invoice.get_id().toHexString()))
                .andExpect(jsonPath("$.amount").value(invoice.getAmount()))
                .andExpect(jsonPath("$.status").value(invoice.getStatus()))
                .andExpect(jsonPath("$.customer.id").value(testCustomer.get_id().toHexString()));
    }

    @Test
    @Story("Create Invoice")
    @DisplayName("POST /invoices persists to MongoDB")
    void createInvoice_PersistsToMongoDB() throws Exception {
        InvoiceCreate invoiceCreate = new InvoiceCreate("pending", 1500.0, testCustomer.get_id().toHexString());

        mockMvc.perform(post("/invoices")
                        .header("Authorization", authHeader("dashboard-invoices-create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceCreate)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status").value("pending"))
                .andExpect(jsonPath("$.amount").value(1500.0));

        // Verify persisted in database
        List<Invoice> invoices = invoiceRepository.findByAudit_DeletedAtIsNull();
        assertThat(invoices).hasSize(1);
        assertThat(invoices.get(0).getAmount()).isEqualTo(1500.0);
        assertThat(invoices.get(0).getStatus()).isEqualTo("pending");
    }

    @Test
    @Story("Update Invoice")
    @DisplayName("PUT /invoices/{id} updates in database")
    void updateInvoice_UpdatesInDatabase() throws Exception {
        Invoice invoice = createAndSaveInvoice(testCustomer, "pending");

        InvoiceUpdate invoiceUpdate = new InvoiceUpdate(
                invoice.get_id().toHexString(),
                "paid",
                2500.0,
                testCustomer.get_id().toHexString()
        );

        mockMvc.perform(put("/invoices/" + invoice.get_id().toHexString())
                        .header("Authorization", authHeader("dashboard-invoices-update"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceUpdate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("paid"))
                .andExpect(jsonPath("$.amount").value(2500.0));

        // Verify updated in database
        Optional<Invoice> updated = invoiceRepository.findBy_idEqualsAndAudit_DeletedAtIsNull(invoice.get_id());
        assertThat(updated).isPresent();
        assertThat(updated.get().getAmount()).isEqualTo(2500.0);
        assertThat(updated.get().getStatus()).isEqualTo("paid");
    }

    @Test
    @Story("Delete Invoice")
    @DisplayName("DELETE /invoices/{id} sets audit.deletedAt")
    void deleteInvoice_SetsAuditDeletedAt() throws Exception {
        Invoice invoice = createAndSaveInvoice(testCustomer);

        mockMvc.perform(delete("/invoices/" + invoice.get_id().toHexString())
                        .header("Authorization", authHeader("dashboard-invoices-delete")))
                .andExpect(status().isOk());

        // Verify soft deleted - should not be found by active query
        Optional<Invoice> deleted = invoiceRepository.findBy_idEqualsAndAudit_DeletedAtIsNull(invoice.get_id());
        assertThat(deleted).isEmpty();

        // But should still exist in DB
        Optional<Invoice> existing = invoiceRepository.findById(invoice.get_id());
        assertThat(existing).isPresent();
        assertThat(existing.get().getAudit().getDeletedAt()).isNotNull();
    }

    @Test
    @Story("Invoice Count")
    @DisplayName("GET /invoices/count returns correct count")
    void getInvoiceCount_ReturnsCorrectCount() throws Exception {
        createAndSaveInvoice(testCustomer);
        createAndSaveInvoice(testCustomer);
        createAndSaveInvoice(testCustomer);

        mockMvc.perform(get("/invoices/count")
                        .header("Authorization", authHeader("dashboard-invoices-read")))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    @Story("Invoice Count By Status")
    @DisplayName("GET /invoices/count with status filter returns correct count")
    void getInvoiceCount_WithStatusFilter_ReturnsCorrectCount() throws Exception {
        createAndSaveInvoice(testCustomer, "pending");
        createAndSaveInvoice(testCustomer, "pending");
        createAndSaveInvoice(testCustomer, "paid");

        mockMvc.perform(get("/invoices/count")
                        .param("status", "pending")
                        .header("Authorization", authHeader("dashboard-invoices-read")))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    @Story("Invoice Amount")
    @DisplayName("GET /invoices/amount returns total amount")
    void getInvoiceAmount_ReturnsTotalAmount() throws Exception {
        Invoice invoice1 = createAndSaveInvoice(testCustomer);
        Invoice invoice2 = createAndSaveInvoice(testCustomer);

        double expectedTotal = invoice1.getAmount() + invoice2.getAmount();

        mockMvc.perform(get("/invoices/amount")
                        .header("Authorization", authHeader("dashboard-invoices-read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedTotal));
    }

    @Test
    @Story("Soft Delete Exclusion")
    @DisplayName("GET /invoices/ excludes soft-deleted invoices")
    void getAllInvoices_ExcludesSoftDeleted() throws Exception {
        Invoice activeInvoice = createAndSaveInvoice(testCustomer);

        // Create and soft-delete an invoice
        Invoice deletedInvoice = createAndSaveInvoice(testCustomer);
        deletedInvoice.setAudit(createDeletedAudit());
        invoiceRepository.save(deletedInvoice);

        mockMvc.perform(get("/invoices/")
                        .header("Authorization", authHeader("dashboard-invoices-read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(activeInvoice.get_id().toHexString()));
    }

    @Test
    @Story("Invoice Not Found")
    @DisplayName("GET /invoices/{id} returns 404 for non-existent invoice")
    void getInvoiceById_Returns404ForNonExistent() throws Exception {
        mockMvc.perform(get("/invoices/507f1f77bcf86cd799439011")
                        .header("Authorization", authHeader("dashboard-invoices-read")))
                .andExpect(status().isNotFound());
    }
}
