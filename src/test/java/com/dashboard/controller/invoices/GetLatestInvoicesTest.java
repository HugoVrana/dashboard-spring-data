package com.dashboard.controller.invoices;

import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.model.entities.Invoice;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Story("Get Latest Invoices")
@DisplayName("GET /invoices/latest")
class GetLatestInvoicesTest extends BaseInvoicesControllerTest {

    @Test
    @DisplayName("should return all invoices when no indices provided")
    void getLatestInvoices_ReturnsAllWhenNoIndices() throws Exception {
        Invoice testInvoice = createTestInvoice();
        InvoiceRead testInvoiceRead = createTestInvoiceRead(testInvoice);
        CustomerRead customerRead = testInvoiceRead.getCustomer();

        when(invoiceService.getAllInvoices()).thenReturn(List.of(testInvoice));
        when(invoiceMapper.toRead(testInvoice)).thenReturn(testInvoiceRead);
        when(customerMapper.toRead(testInvoice.getCustomer())).thenReturn(customerRead);

        mockMvc.perform(get("/invoices/latest"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("should return invoices in range when indices provided")
    void getLatestInvoices_ReturnsRangeWhenIndicesProvided() throws Exception {
        Invoice testInvoice = createTestInvoice();
        InvoiceRead testInvoiceRead = createTestInvoiceRead(testInvoice);
        CustomerRead customerRead = testInvoiceRead.getCustomer();

        when(invoiceService.getLatestInvoice(0, 5)).thenReturn(List.of(testInvoice));
        when(invoiceMapper.toRead(testInvoice)).thenReturn(testInvoiceRead);
        when(customerMapper.toRead(testInvoice.getCustomer())).thenReturn(customerRead);

        mockMvc.perform(get("/invoices/latest")
                        .param("indexFrom", "0")
                        .param("indexTo", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("should return 500 when indexFrom > indexTo")
    void getLatestInvoices_ThrowsWhenIndexFromGreaterThanIndexTo() throws Exception {
        mockMvc.perform(get("/invoices/latest")
                        .param("indexFrom", "10")
                        .param("indexTo", "5"))
                .andExpect(status().isInternalServerError());
    }
}
