package com.dashboard.service.invoice;

import com.dashboard.model.entities.Invoice;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Story("Insert Invoice")
@DisplayName("insertInvoice")
public class InsertInvoiceTest extends BaseInvoiceServiceTest {
    @Test
    @DisplayName("should insert invoice and sync with search service")
    void insertInvoice_InsertsAndSyncsInvoice() {
        when(invoiceRepository.insert(testInvoice)).thenReturn(testInvoice);

        Invoice result = invoiceService.insertInvoice(testInvoice);

        assertThat(result).isEqualTo(testInvoice);
        verify(invoiceRepository).insert(testInvoice);
        verify(invoiceSearchService).syncInvoice(testInvoice);
    }
}
