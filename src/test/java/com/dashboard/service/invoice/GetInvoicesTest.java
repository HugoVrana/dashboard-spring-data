package com.dashboard.service.invoice;

import com.dashboard.common.model.Audit;
import com.dashboard.model.entities.Invoice;
import io.qameta.allure.Story;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Story("Get Latest Invoices")
@DisplayName("getLatestInvoice")
public class GetInvoicesTest extends BaseInvoiceServiceTest {
    @Test
    @DisplayName("should return latest invoices within range")
    void getLatestInvoice_ReturnsLatestInvoicesWithinRange() {
        Invoice invoice1 = new Invoice();
        invoice1.set_id(new ObjectId());
        invoice1.setDate(LocalDate.now().minusDays(1));
        invoice1.setAudit(new Audit());

        Invoice invoice2 = new Invoice();
        invoice2.set_id(new ObjectId());
        invoice2.setDate(LocalDate.now());
        invoice2.setAudit(new Audit());

        List<Invoice> allInvoices = List.of(invoice1, invoice2);
        when(invoiceRepository.findByAudit_DeletedAtIsNull()).thenReturn(allInvoices);

        List<Invoice> result = invoiceService.getLatestInvoice(0, 1);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDate()).isAfterOrEqualTo(result.get(1).getDate());
        verify(invoiceRepository).findByAudit_DeletedAtIsNull();
    }

    @Test
    @DisplayName("should return empty list when no invoices exist")
    void getLatestInvoice_ReturnsEmptyListWhenNoInvoices() {
        when(invoiceRepository.findByAudit_DeletedAtIsNull()).thenReturn(Collections.emptyList());

        List<Invoice> result = invoiceService.getLatestInvoice(0, 5);

        assertThat(result).isEmpty();
        verify(invoiceRepository).findByAudit_DeletedAtIsNull();
    }

    @Test
    @DisplayName("should skip invoices based on indexFrom")
    void getLatestInvoice_SkipsInvoicesBasedOnIndexFrom() {
        Invoice invoice1 = new Invoice();
        invoice1.set_id(new ObjectId());
        invoice1.setDate(LocalDate.now().minusDays(2));
        invoice1.setAudit(new Audit());

        Invoice invoice2 = new Invoice();
        invoice2.set_id(new ObjectId());
        invoice2.setDate(LocalDate.now().minusDays(1));
        invoice2.setAudit(new Audit());

        Invoice invoice3 = new Invoice();
        invoice3.set_id(new ObjectId());
        invoice3.setDate(LocalDate.now());
        invoice3.setAudit(new Audit());

        List<Invoice> allInvoices = List.of(invoice1, invoice2, invoice3);
        when(invoiceRepository.findByAudit_DeletedAtIsNull()).thenReturn(allInvoices);

        List<Invoice> result = invoiceService.getLatestInvoice(1, 2);

        assertThat(result).hasSize(2);
        verify(invoiceRepository).findByAudit_DeletedAtIsNull();
    }
}
