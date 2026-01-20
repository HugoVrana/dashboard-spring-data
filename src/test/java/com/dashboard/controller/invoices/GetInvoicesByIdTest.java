package com.dashboard.controller.invoices;

import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.model.entities.Invoice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("GET /invoices/{id}")
public class GetInvoicesByIdTest extends BaseInvoicesControllerTest {

    @Test
    @DisplayName("should return invoice when found")
    void getInvoiceById_ReturnsInvoiceWhenFound() throws Exception {
        Invoice testInvoice = createTestInvoice();
        InvoiceRead testInvoiceRead = createTestInvoiceRead(testInvoice);
        CustomerRead customerRead = testInvoiceRead.getCustomer();

        when(invoiceService.getInvoiceById(testInvoiceId)).thenReturn(Optional.of(testInvoice));
        when(invoiceMapper.toRead(testInvoice)).thenReturn(testInvoiceRead);
        when(customerMapper.toRead(testInvoice.getCustomer())).thenReturn(customerRead);

        mockMvc.perform(get("/invoices/{id}", testInvoiceId.toHexString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testInvoiceId.toHexString()))
                .andExpect(jsonPath("$.amount").value(testAmount))
                .andExpect(jsonPath("$.status").value(testStatus));
    }

    @Test
    @DisplayName("should return 404 when invoice not found")
    void getInvoiceById_Returns404WhenNotFound() throws Exception {
        when(invoiceService.getInvoiceById(testInvoiceId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/invoices/{id}", testInvoiceId.toHexString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 404 when id is invalid")
    void getInvoiceById_Returns404WhenIdInvalid() throws Exception {
        mockMvc.perform(get("/invoices/{id}", "invalid-id"))
                .andExpect(status().isNotFound());
    }
}
