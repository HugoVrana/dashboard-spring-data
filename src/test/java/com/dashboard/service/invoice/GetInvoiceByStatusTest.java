package com.dashboard.service.invoice;

import com.dashboard.common.model.Audit;
import com.dashboard.model.entities.Invoice;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetInvoiceByStatusTest extends BaseInvoiceServiceTest {

    @Test
    @DisplayName("should return invoices with pending status")
    void getInvoicesByStatus_ReturnsInvoicesWithPendingStatus() {
        String status = "pending";
        List<Invoice> expectedInvoices = List.of(testInvoice);
        when(invoiceRepository.findByStatusAndAudit_DeletedAtIsNull(status))
                .thenReturn(expectedInvoices);

        List<Invoice> result = invoiceService.getInvoicesByStatus(status);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getStatus()).isEqualTo(status);
        verify(invoiceRepository).findByStatusAndAudit_DeletedAtIsNull(status);
    }

    @Test
    @DisplayName("should return invoices with paid status")
    void getInvoicesByStatus_ReturnsInvoicesWithPaidStatus() {
        String status = "paid";
        Invoice paidInvoice = new Invoice();
        paidInvoice.set_id(new ObjectId());
        paidInvoice.setStatus("paid");
        paidInvoice.setAudit(new Audit());
        List<Invoice> expectedInvoices = List.of(paidInvoice);
        when(invoiceRepository.findByStatusAndAudit_DeletedAtIsNull(status))
                .thenReturn(expectedInvoices);

        List<Invoice> result = invoiceService.getInvoicesByStatus(status);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getStatus()).isEqualTo("paid");
        verify(invoiceRepository).findByStatusAndAudit_DeletedAtIsNull(status);
    }

    @Test
    @DisplayName("should return empty list when no invoices match status")
    void getInvoicesByStatus_ReturnsEmptyListWhenNoMatch() {
        String status = "cancelled";
        when(invoiceRepository.findByStatusAndAudit_DeletedAtIsNull(status))
                .thenReturn(Collections.emptyList());

        List<Invoice> result = invoiceService.getInvoicesByStatus(status);

        assertThat(result).isEmpty();
        verify(invoiceRepository).findByStatusAndAudit_DeletedAtIsNull(status);
    }
}
