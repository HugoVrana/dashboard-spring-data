package com.dashboard.mapper.interfaces;

import com.dashboard.dataTransferObject.invoice.InvoiceCreate;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.dataTransferObject.invoice.InvoiceUpdate;
import com.dashboard.model.entities.Customer;
import com.dashboard.model.entities.Invoice;

public interface IInvoiceMapper {

    InvoiceRead toRead(Invoice invoice);

    InvoiceRead toReadWithCustomer(Invoice invoice);

    Invoice toModel(InvoiceCreate invoiceCreate, Customer customer);

    Invoice toModel(InvoiceUpdate invoiceUpdate, Customer customer);
}
