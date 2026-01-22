package com.dashboard.controller.invoices;

import com.dashboard.model.entities.InvoiceSearchDocument;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Story("Get Invoice Pages")
@DisplayName("GET /invoices/pages")
public class GetInvoicePagesTest extends BaseInvoicesControllerTest {
    @Test
    @DisplayName("should return total pages")
    void getPages_ReturnsTotalPages() throws Exception {
        Page<InvoiceSearchDocument> page = new PageImpl<>(Collections.emptyList(), Pageable.ofSize(15), 45);

        when(invoiceSearchService.search(any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/invoices/pages"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("3"));
    }

    @Test
    @DisplayName("should use custom size when provided")
    void getPages_UsesCustomSize() throws Exception {
        Page<InvoiceSearchDocument> page = new PageImpl<>(Collections.emptyList(), Pageable.ofSize(10), 25);

        when(invoiceSearchService.search(any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/invoices/pages")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("3"));
    }
}
