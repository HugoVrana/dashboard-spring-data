package com.dashboard.controller.invoices;

import com.dashboard.model.entities.Invoice;
import io.qameta.allure.Story;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Story("Get Invoice Amount")
@DisplayName("GET /invoices/amount")
public class GetInvoicesAmountTest extends BaseInvoicesControllerTest {

    @Test
    @DisplayName("should return total amount when no status provided")
    void getInvoiceAmount_ReturnsTotalAmount() throws Exception {
        Invoice invoice1 = createTestInvoice();
        invoice1.setAmount(new BigDecimal("100.00"));
        Invoice invoice2 = createTestInvoice();
        invoice2.set_id(new ObjectId());
        invoice2.setAmount(new BigDecimal("200.00"));

        when(invoiceService.getAllInvoices()).thenReturn(List.of(invoice1, invoice2));

        mockMvc.perform(get("/invoices/amount"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("300.00"));
    }

    @Test
    @DisplayName("should return amount by status when status provided")
    void getInvoiceAmount_ReturnsAmountByStatus() throws Exception {
        Invoice testInvoice = createTestInvoice();
        testInvoice.setAmount(new BigDecimal("150.00"));

        when(invoiceService.getInvoicesByStatus("paid")).thenReturn(List.of(testInvoice));

        mockMvc.perform(get("/invoices/amount")
                        .param("status", "paid"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("150.00"));
    }
}
