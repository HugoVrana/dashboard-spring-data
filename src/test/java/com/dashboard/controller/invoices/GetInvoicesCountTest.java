package com.dashboard.controller.invoices;

import com.dashboard.model.entities.Invoice;
import io.qameta.allure.Story;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Story("Get Invoice Count")
@DisplayName("GET /invoices/count")
class GetInvoicesCountTest extends BaseInvoicesControllerTest {
    @Test
    @DisplayName("should return total count when no status provided")
    void getInvoiceCount_ReturnsTotalCount() throws Exception {
        Invoice invoice1 = createTestInvoice();
        Invoice invoice2 = createTestInvoice();
        invoice2.set_id(new ObjectId());

        when(invoiceService.getAllInvoices()).thenReturn(List.of(invoice1, invoice2));

        mockMvc.perform(get("/invoices/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("2"));
    }

    @Test
    @DisplayName("should return count by status when status provided")
    void getInvoiceCount_ReturnsCountByStatus() throws Exception {
        Invoice testInvoice = createTestInvoice();

        when(invoiceService.getInvoicesByStatus("pending")).thenReturn(List.of(testInvoice));

        mockMvc.perform(get("/invoices/count")
                        .param("status", "pending"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("1"));
    }
}
