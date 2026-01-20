package com.dashboard.service.invoice;

import com.dashboard.model.entities.Invoice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("InvoiceService - searchInvoices")
class SearchInvoicesServiceTest extends BaseInvoiceServiceTest {

    @Test
    @DisplayName("should return all invoices when search term is null")
    void searchInvoices_ReturnsAllInvoicesWhenTermIsNull() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Invoice> invoices = List.of(testInvoice);
        when(mongoTemplate.find(any(Query.class), eq(Invoice.class))).thenReturn(invoices);
        when(mongoTemplate.count(any(Query.class), eq(Invoice.class))).thenReturn(1L);

        Page<Invoice> result = invoiceService.searchInvoices(null, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(mongoTemplate).find(any(Query.class), eq(Invoice.class));
    }

    @Test
    @DisplayName("should return all invoices when search term is empty")
    void searchInvoices_ReturnsAllInvoicesWhenTermIsEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Invoice> invoices = List.of(testInvoice);
        when(mongoTemplate.find(any(Query.class), eq(Invoice.class))).thenReturn(invoices);
        when(mongoTemplate.count(any(Query.class), eq(Invoice.class))).thenReturn(1L);

        Page<Invoice> result = invoiceService.searchInvoices("", pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(mongoTemplate).find(any(Query.class), eq(Invoice.class));
    }

    @Test
    @DisplayName("should return all invoices when search term is whitespace")
    void searchInvoices_ReturnsAllInvoicesWhenTermIsWhitespace() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Invoice> invoices = List.of(testInvoice);
        when(mongoTemplate.find(any(Query.class), eq(Invoice.class))).thenReturn(invoices);
        when(mongoTemplate.count(any(Query.class), eq(Invoice.class))).thenReturn(1L);

        Page<Invoice> result = invoiceService.searchInvoices("   ", pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(mongoTemplate).find(any(Query.class), eq(Invoice.class));
    }

    @Test
    @DisplayName("should search by ObjectId when term is valid ObjectId")
    void searchInvoices_SearchesByObjectIdWhenTermIsValidObjectId() {
        Pageable pageable = PageRequest.of(0, 10);
        String objectIdTerm = testInvoiceId.toHexString();
        List<Invoice> invoices = List.of(testInvoice);
        when(mongoTemplate.find(any(Query.class), eq(Invoice.class))).thenReturn(invoices);

        Page<Invoice> result = invoiceService.searchInvoices(objectIdTerm, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(mongoTemplate).find(any(Query.class), eq(Invoice.class));
    }
}
