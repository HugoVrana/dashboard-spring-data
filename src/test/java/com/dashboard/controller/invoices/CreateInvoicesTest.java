package com.dashboard.controller.invoices;

import com.dashboard.dataTransferObject.invoice.InvoiceCreate;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.common.model.exception.NotFoundException;
import com.dashboard.model.entities.Invoice;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Story("Create Invoice")
@DisplayName("POST /invoices")
public class CreateInvoicesTest extends BaseInvoicesControllerTest {

    @Test
    @DisplayName("should create invoice successfully")
    void createInvoice_CreatesInvoiceSuccessfully() throws Exception {
        Invoice testInvoice = createTestInvoice();
        InvoiceRead testInvoiceRead = createTestInvoiceRead(testInvoice);

        InvoiceCreate invoiceCreate = new InvoiceCreate("pending", new BigDecimal("1000.00"), testCustomerId.toHexString());

        when(invoiceService.createInvoice(any(InvoiceCreate.class))).thenReturn(testInvoiceRead);

        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceCreate)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status").value(testStatus));
    }

    @Test
    @DisplayName("should return 500 when customer not found (NotFoundException not handled as 404)")
    void createInvoice_Returns500WhenCustomerNotFound() throws Exception {
        InvoiceCreate invoiceCreate = new InvoiceCreate("pending", new BigDecimal("1000.00"), testCustomerId.toHexString());

        when(invoiceService.createInvoice(any(InvoiceCreate.class)))
                .thenThrow(new NotFoundException("The provided customer id does not exist"));

        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceCreate)))
                .andExpect(status().isInternalServerError());
    }
}
