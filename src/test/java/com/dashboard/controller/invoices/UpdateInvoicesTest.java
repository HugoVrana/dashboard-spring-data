package com.dashboard.controller.invoices;

import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.dataTransferObject.invoice.InvoiceUpdate;
import com.dashboard.model.entities.Customer;
import com.dashboard.model.entities.Invoice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("PUT /invoices/{id}")
public class UpdateInvoicesTest extends BaseInvoicesControllerTest {

    @Test
    @DisplayName("should update invoice successfully")
    void updateInvoice_UpdatesInvoiceSuccessfully() throws Exception {
        Invoice testInvoice = createTestInvoice();
        Customer customer = testInvoice.getCustomer();
        InvoiceRead testInvoiceRead = createTestInvoiceRead(testInvoice);
        CustomerRead customerRead = testInvoiceRead.getCustomer();

        InvoiceUpdate invoiceUpdate = new InvoiceUpdate(testInvoiceId.toHexString(), "paid", 1500.0, testCustomerId.toHexString());

        when(invoiceService.getInvoiceById(testInvoiceId)).thenReturn(Optional.of(testInvoice));
        when(customersService.getCustomer(testCustomerId)).thenReturn(Optional.of(customer));
        when(invoiceMapper.toModel(any(InvoiceUpdate.class), any(Customer.class))).thenReturn(testInvoice);
        when(invoiceService.updateInvoice(any(Invoice.class))).thenReturn(testInvoice);
        when(invoiceMapper.toRead(testInvoice)).thenReturn(testInvoiceRead);
        when(customerMapper.toRead(customer)).thenReturn(customerRead);

        mockMvc.perform(put("/invoices/{id}", testInvoiceId.toHexString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceUpdate)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    @DisplayName("should return 404 when invoice not found")
    void updateInvoice_Returns404WhenInvoiceNotFound() throws Exception {
        InvoiceUpdate invoiceUpdate = new InvoiceUpdate(testInvoiceId.toHexString(), "paid", 1500.0, testCustomerId.toHexString());

        when(invoiceService.getInvoiceById(testInvoiceId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/invoices/{id}", testInvoiceId.toHexString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceUpdate)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 404 when id is invalid")
    void updateInvoice_Returns404WhenIdInvalid() throws Exception {
        InvoiceUpdate invoiceUpdate = new InvoiceUpdate("invalid-id", "paid", 1500.0, testCustomerId.toHexString());

        mockMvc.perform(put("/invoices/{id}", "invalid-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceUpdate)))
                .andExpect(status().isNotFound());
    }
}
