package com.dashboard.controller.invoices;

import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.dataTransferObject.page.PageRequest;
import com.dashboard.model.entities.Invoice;
import com.dashboard.model.entities.InvoiceSearchDocument;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Story("Search Invoices")
@DisplayName("POST /invoices/search")
public class SearchInvoicesTest extends BaseInvoicesControllerTest {

    @Test
    @DisplayName("should return paginated results when search succeeds")
    void searchInvoices_ReturnsPaginatedResults() throws Exception {
        Invoice testInvoice = createTestInvoice();
        InvoiceSearchDocument searchDoc = createTestInvoiceSearchDocument(testInvoice);
        InvoiceRead testInvoiceRead = createTestInvoiceRead(testInvoice);

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(1);
        pageRequest.setSize(10);
        pageRequest.setSearch("test");

        Page<InvoiceSearchDocument> searchPage = new PageImpl<>(List.of(searchDoc), Pageable.ofSize(10), 1);

        when(invoiceSearchService.search(eq("test"), any(Pageable.class))).thenReturn(searchPage);
        when(invoiceSearchMapper.toRead(searchDoc)).thenReturn(testInvoiceRead);

        mockMvc.perform(post("/invoices/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(1));
    }

    @Test
    @DisplayName("should return 204 when no results found")
    void searchInvoices_Returns204WhenNoResults() throws Exception {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(1);
        pageRequest.setSize(10);
        pageRequest.setSearch("nonexistent");

        Page<InvoiceSearchDocument> emptyPage = Page.empty();
        when(invoiceSearchService.search(eq("nonexistent"), any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(post("/invoices/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("should return 500 when page is zero or negative")
    void searchInvoices_ThrowsWhenPageIsInvalid() throws Exception {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(0);
        pageRequest.setSize(10);
        pageRequest.setSearch("test");

        mockMvc.perform(post("/invoices/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isInternalServerError());
    }
}
