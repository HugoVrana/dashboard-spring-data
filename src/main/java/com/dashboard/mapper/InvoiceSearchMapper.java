package com.dashboard.mapper;

import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.mapper.interfaces.IInvoiceSearchMapper;
import com.dashboard.model.entities.InvoiceSearchDocument;
import org.springframework.stereotype.Service;

@Service
public class InvoiceSearchMapper implements IInvoiceSearchMapper {

    @Override
    public InvoiceRead toRead(InvoiceSearchDocument doc) {
        InvoiceRead invoiceRead = new InvoiceRead();
        invoiceRead.setId(doc.getInvoiceId().toHexString());
        invoiceRead.setAmount(doc.getAmount());
        invoiceRead.setStatus(doc.getStatus());
        invoiceRead.setDate(doc.getDate());

        CustomerRead customerRead = new CustomerRead();
        customerRead.setId(doc.getCustomerId().toHexString());
        customerRead.setName(doc.getCustomerName());
        customerRead.setEmail(doc.getCustomerEmail());
        customerRead.setImageUrl(doc.getCustomerImageUrl());

        invoiceRead.setCustomer(customerRead);

        return invoiceRead;
    }
}
