package com.dashboard.mapper;

import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.mapper.interfaces.IInvoiceMapper;
import com.dashboard.model.Invoice;
import org.springframework.stereotype.Service;

@Service
public class InvoiceMapper implements IInvoiceMapper {

    @Override
    public InvoiceRead toRead(Invoice invoice) {
        InvoiceRead invoiceRead = new InvoiceRead();
        invoiceRead.setId(invoice._id.toHexString());
        invoiceRead.setAmount(invoice.getAmount());
        invoiceRead.setStatus(invoice.getStatus());
        invoiceRead.setDate(invoice.getDate());
        return invoiceRead;
    }
}
