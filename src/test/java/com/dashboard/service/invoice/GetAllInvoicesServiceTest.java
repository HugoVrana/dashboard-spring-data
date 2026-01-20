package com.dashboard.service.invoice;

import com.dashboard.model.entities.Invoice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("InvoiceService - getAllInvoices")
class GetAllInvoicesServiceTest extends BaseInvoiceServiceTest {

    @Test
    @DisplayName("should return all non-deleted invoices")
    void getAllInvoices_ReturnsAllNonDeletedInvoices() {
        List<Invoice> expectedInvoices = List.of(testInvoice);
        when(invoiceRepository.findByAudit_DeletedAtIsNull()).thenReturn(expectedInvoices);

        List<Invoice> result = invoiceService.getAllInvoices();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(testInvoice);
        verify(invoiceRepository).findByAudit_DeletedAtIsNull();
    }

    @Test
    @DisplayName("should return empty list when no invoices exist")
    void getAllInvoices_ReturnsEmptyListWhenNoInvoices() {
        when(invoiceRepository.findByAudit_DeletedAtIsNull()).thenReturn(Collections.emptyList());

        List<Invoice> result = invoiceService.getAllInvoices();

        assertThat(result).isEmpty();
        verify(invoiceRepository).findByAudit_DeletedAtIsNull();
    }
}
