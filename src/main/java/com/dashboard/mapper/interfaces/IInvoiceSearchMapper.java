package com.dashboard.mapper.interfaces;

import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.model.entities.InvoiceSearchDocument;

public interface IInvoiceSearchMapper {

    InvoiceRead toRead(InvoiceSearchDocument doc);
}
