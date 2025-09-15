package com.dashboard.mapper.interfaces;

import com.dashboard.dataTransferObject.invoice.InvoiceCreate;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.model.Customer;
import com.dashboard.model.Invoice;

public interface IInvoiceMapper {

    InvoiceRead toRead(Invoice invoice);

    Invoice toModel(InvoiceCreate invoiceCreate, Customer customer);
}
