package com.dashboard.service.invoice;

import com.dashboard.common.model.exception.ResourceNotFoundException;
import com.dashboard.model.entities.Invoice;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

        Invoice result = invoiceService.getInvoiceById(testInvoiceId.toHexString());

        assertThat(result).isEqualTo(testInvoice);
        verify(invoiceRepository).findBy_idEqualsAndAudit_DeletedAtIsNull(testInvoiceId);
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when invoice not found")
    void getInvoiceById_ThrowsWhenNotFound() {
        when(invoiceRepository.findBy_idEqualsAndAudit_DeletedAtIsNull(testInvoiceId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceService.getInvoiceById(testInvoiceId.toHexString()))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(invoiceRepository).findBy_idEqualsAndAudit_DeletedAtIsNull(testInvoiceId);
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when id is invalid")
    void getInvoiceById_ThrowsWhenIdInvalid() {
        assertThatThrownBy(() -> invoiceService.getInvoiceById("invalid-id"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
