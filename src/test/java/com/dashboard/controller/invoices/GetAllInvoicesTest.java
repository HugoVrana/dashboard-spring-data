package com.dashboard.controller.invoices;

import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.model.entities.Invoice;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Story("Get All Invoices")
@DisplayName("GET /invoices")
class GetAllInvoicesTest extends BaseInvoicesControllerTest {

    @Test
    @DisplayName("should return all invoices")
    void getAllInvoices_ReturnsAllInvoices() throws Exception {
        Invoice testInvoice = createTestInvoice();
        InvoiceRead testInvoiceRead = createTestInvoiceRead(testInvoice);
        CustomerRead customerRead = testInvoiceRead.getCustomer();

        when(invoiceService.getAllInvoices()).thenReturn(List.of(testInvoice));
        when(invoiceMapper.toRead(testInvoice)).thenReturn(testInvoiceRead);
        when(customerMapper.toRead(testInvoice.getCustomer())).thenReturn(customerRead);

        mockMvc.perform(get("/invoices/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(testInvoiceId.toHexString()))
                .andExpect(jsonPath("$[0].amount").value(testAmount))
                .andExpect(jsonPath("$[0].status").value(testStatus));
    }

    @Test
    @DisplayName("should return empty list when no invoices exist")
    void getAllInvoices_ReturnsEmptyListWhenNoInvoices() throws Exception {
        when(invoiceService.getAllInvoices()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/invoices/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}