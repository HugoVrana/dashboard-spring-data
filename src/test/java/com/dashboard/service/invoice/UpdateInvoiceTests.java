package com.dashboard.service.invoice;

import com.dashboard.model.entities.Invoice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName( "updateInvoice")
public class UpdateInvoiceTests extends BaseInvoiceServiceTest {

    @Test
    @DisplayName("should update invoice and sync with search service")
    void updateInvoice_UpdatesAndSyncsInvoice() {
        testInvoice.setStatus("paid");
        when(invoiceRepository.save(testInvoice)).thenReturn(testInvoice);

        Invoice result = invoiceService.updateInvoice(testInvoice);

        assertThat(result).isEqualTo(testInvoice);
        assertThat(result.getStatus()).isEqualTo("paid");
        verify(invoiceRepository).save(testInvoice);
        verify(invoiceSearchService).syncInvoice(testInvoice);
    }
}
