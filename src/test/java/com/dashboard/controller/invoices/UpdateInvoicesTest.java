package com.dashboard.controller.invoices;

import com.dashboard.common.model.exception.ResourceNotFoundException;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.dataTransferObject.invoice.InvoiceUpdate;
import com.dashboard.model.entities.Invoice;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Story("Update Invoice")
@DisplayName("PUT /invoices/{id}")
public class UpdateInvoicesTest extends BaseInvoicesControllerTest {

    @Test
    @DisplayName("should update invoice successfully")
    void updateInvoice_UpdatesInvoiceSuccessfully() throws Exception {
        Invoice testInvoice = createTestInvoice();
        InvoiceRead testInvoiceRead = createTestInvoiceRead(testInvoice);

        InvoiceUpdate invoiceUpdate = new InvoiceUpdate(testInvoiceId.toHexString(), "paid", new BigDecimal("1500.00"), testCustomerId.toHexString());

        when(invoiceService.updateInvoice(eq(testInvoiceId.toHexString()), any(InvoiceUpdate.class)))
                .thenReturn(testInvoiceRead);

        mockMvc.perform(put("/invoices/{id}", testInvoiceId.toHexString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceUpdate)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    @DisplayName("should return 404 when invoice not found")
    void updateInvoice_Returns404WhenInvoiceNotFound() throws Exception {
        InvoiceUpdate invoiceUpdate = new InvoiceUpdate(testInvoiceId.toHexString(), "paid", new BigDecimal("1500.00"), testCustomerId.toHexString());

        when(invoiceService.updateInvoice(eq(testInvoiceId.toHexString()), any(InvoiceUpdate.class)))
                .thenThrow(new ResourceNotFoundException("Invoice not found"));

        mockMvc.perform(put("/invoices/{id}", testInvoiceId.toHexString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceUpdate)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 404 when id is invalid")
    void updateInvoice_Returns404WhenIdInvalid() throws Exception {
        InvoiceUpdate invoiceUpdate = new InvoiceUpdate("invalid-id", "paid", new BigDecimal("1500.00"), testCustomerId.toHexString());

        when(invoiceService.updateInvoice(eq("invalid-id"), any(InvoiceUpdate.class)))
                .thenThrow(new ResourceNotFoundException("This id is invalid"));

        mockMvc.perform(put("/invoices/{id}", "invalid-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceUpdate)))
                .andExpect(status().isNotFound());
    }
}
