package com.dashboard.service.invoice;

import com.dashboard.model.entities.Invoice;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Story("Get Invoice By ID")
@DisplayName("getInvoiceById")
public class GetInvoiceByIdTest extends BaseInvoiceServiceTest {
    @Test
    @DisplayName("should return invoice when found by id")
    void getInvoiceById_ReturnsInvoiceWhenFound() {
        when(invoiceRepository.findBy_idEqualsAndAudit_DeletedAtIsNull(testInvoiceId))
                .thenReturn(Optional.of(testInvoice));

        Optional<Invoice> result = invoiceService.getInvoiceById(testInvoiceId);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testInvoice);
        verify(invoiceRepository).findBy_idEqualsAndAudit_DeletedAtIsNull(testInvoiceId);
    }

    @Test
    @DisplayName("should return empty when invoice not found")
    void getInvoiceById_ReturnsEmptyWhenNotFound() {
        when(invoiceRepository.findBy_idEqualsAndAudit_DeletedAtIsNull(testInvoiceId))
                .thenReturn(Optional.empty());

        Optional<Invoice> result = invoiceService.getInvoiceById(testInvoiceId);

        assertThat(result).isEmpty();
        verify(invoiceRepository).findBy_idEqualsAndAudit_DeletedAtIsNull(testInvoiceId);
    }
}
