package com.dashboard.controller.invoices;

import com.dashboard.model.entities.Invoice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("DELETE /invoices")
public class DeleteInvoiceTest extends BaseInvoicesControllerTest {
    @Test
    @DisplayName("should delete invoice successfully")
    void deleteInvoice_DeletesInvoiceSuccessfully() throws Exception {
        Invoice testInvoice = createTestInvoice();

        when(invoiceService.getInvoiceById(testInvoiceId))
                .thenReturn(Optional.of(testInvoice))
                .thenReturn(Optional.empty());
        when(invoiceService.updateInvoice(any(Invoice.class))).thenReturn(testInvoice);

        mockMvc.perform(delete("/invoices/{id}", testInvoiceId.toHexString()))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("should return 404 when invoice not found")
    void deleteInvoice_Returns404WhenNotFound() throws Exception {
        when(invoiceService.getInvoiceById(testInvoiceId)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/invoices/{id}", testInvoiceId.toHexString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 404 when id is invalid")
    void deleteInvoice_Returns404WhenIdInvalid() throws Exception {
        mockMvc.perform(delete("/invoices/{id}", "invalid-id"))
                .andExpect(status().isNotFound());
    }
}
