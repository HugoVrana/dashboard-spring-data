package com.dashboard.mappers;

import com.dashboard.dataTransferObjects.InvoiceDto;
import com.dashboard.model.Invoice;

public interface IInvoiceMapper {

    InvoiceDto toDto(Invoice invoice);
}
