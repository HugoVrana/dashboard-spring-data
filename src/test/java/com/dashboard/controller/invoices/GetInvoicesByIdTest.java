package com.dashboard.controller.invoices;

import com.dashboard.common.model.exception.ResourceNotFoundException;
import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.model.entities.Invoice;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Story("Get Invoice By ID")
@DisplayName("GET /invoices/{id}")
public class GetInvoicesByIdTest extends BaseInvoicesControllerTest {

    @Test
    @DisplayName("should return invoice when found")
    void getInvoiceById_ReturnsInvoiceWhenFound() throws Exception {
        Invoice testInvoice = createTestInvoice();
        InvoiceRead testInvoiceRead = createTestInvoiceRead(testInvoice);
        CustomerRead customerRead = testInvoiceRead.getCustomer();

        when(invoiceService.getInvoiceById(testInvoiceId.toHexString())).thenReturn(testInvoice);
        when(invoiceMapper.toReadWithCustomer(testInvoice)).thenReturn(testInvoiceRead);

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
        when(invoiceService.getInvoiceById(testInvoiceId.toHexString()))
                .thenThrow(new ResourceNotFoundException("Invoice not found"));

        mockMvc.perform(get("/invoices/{id}", testInvoiceId.toHexString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 404 when id is invalid")
    void getInvoiceById_Returns404WhenIdInvalid() throws Exception {
        when(invoiceService.getInvoiceById("invalid-id"))
                .thenThrow(new ResourceNotFoundException("This id is invalid"));

        mockMvc.perform(get("/invoices/{id}", "invalid-id"))
                .andExpect(status().isNotFound());
    }
}
