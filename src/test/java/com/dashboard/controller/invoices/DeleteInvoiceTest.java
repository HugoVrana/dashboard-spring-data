package com.dashboard.controller.invoices;

import com.dashboard.common.model.exception.ResourceNotFoundException;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Story("Delete Invoice")
@DisplayName("DELETE /invoices")
public class DeleteInvoiceTest extends BaseInvoicesControllerTest {
    @Test
    @DisplayName("should delete invoice successfully")
    void deleteInvoice_DeletesInvoiceSuccessfully() throws Exception {
        doNothing().when(invoiceService).deleteInvoice(testInvoiceId.toHexString());

        mockMvc.perform(delete("/invoices/{id}", testInvoiceId.toHexString()))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("should return 404 when invoice not found")
    void deleteInvoice_Returns404WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Invoice not found"))
                .when(invoiceService).deleteInvoice(testInvoiceId.toHexString());

        mockMvc.perform(delete("/invoices/{id}", testInvoiceId.toHexString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 404 when id is invalid")
    void deleteInvoice_Returns404WhenIdInvalid() throws Exception {
        doThrow(new ResourceNotFoundException("This id is invalid"))
                .when(invoiceService).deleteInvoice("invalid-id");

        mockMvc.perform(delete("/invoices/{id}", "invalid-id"))
                .andExpect(status().isNotFound());
    }
}
