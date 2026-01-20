package com.dashboard.controller.invoices;

import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.dataTransferObject.invoice.InvoiceCreate;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.model.entities.Customer;
import com.dashboard.model.entities.Invoice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("POST /invoices")
public class CreateInvoicesTest extends BaseInvoicesControllerTest {

    @Test
    @DisplayName("should create invoice successfully")
    void createInvoice_CreatesInvoiceSuccessfully() throws Exception {
        Invoice testInvoice = createTestInvoice();
        Customer customer = testInvoice.getCustomer();
        InvoiceRead testInvoiceRead = createTestInvoiceRead(testInvoice);
        CustomerRead customerRead = testInvoiceRead.getCustomer();

        InvoiceCreate invoiceCreate = new InvoiceCreate("pending", 1000.0, testCustomerId.toHexString());

        when(customersService.getCustomer(testCustomerId)).thenReturn(Optional.of(customer));
        when(invoiceMapper.toModel(any(InvoiceCreate.class), any(Customer.class))).thenReturn(testInvoice);
        when(invoiceService.insertInvoice(any(Invoice.class))).thenReturn(testInvoice);
        when(invoiceMapper.toRead(testInvoice)).thenReturn(testInvoiceRead);
        when(customerMapper.toRead(customer)).thenReturn(customerRead);

        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceCreate)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status").value(testStatus));
    }

    @Test
    @DisplayName("should return 500 when customer not found (NotFoundException not handled)")
    void createInvoice_Returns500WhenCustomerNotFound() throws Exception {
        InvoiceCreate invoiceCreate = new InvoiceCreate("pending", 1000.0, testCustomerId.toHexString());

        when(customersService.getCustomer(testCustomerId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceCreate)))
                .andExpect(status().isInternalServerError());
    }
}
