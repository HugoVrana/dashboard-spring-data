package com.dashboard.mappers;

import com.dashboard.dataTransferObjects.invoice.InvoiceRead;
import com.dashboard.model.Invoice;

public interface IInvoiceMapper {

    InvoiceRead toRead(Invoice invoice);
}
