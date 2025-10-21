package com.dashboard.mapper;

import com.dashboard.dataTransferObject.invoice.InvoiceCreate;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.dataTransferObject.invoice.InvoiceUpdate;
import com.dashboard.mapper.interfaces.IInvoiceMapper;
import com.dashboard.model.entities.Customer;
import com.dashboard.model.entities.Invoice;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
public class InvoiceMapper implements IInvoiceMapper {

    @Override
    public InvoiceRead toRead(Invoice invoice) {
        InvoiceRead invoiceRead = new InvoiceRead();
        invoiceRead.setId(invoice.get_id().toHexString());
        invoiceRead.setAmount(invoice.getAmount());
        invoiceRead.setStatus(invoice.getStatus());
        invoiceRead.setDate(invoice.getDate());
        return invoiceRead;
    }

    @Override
    public Invoice toModel(InvoiceCreate invoiceCreate, Customer customer) {
        Invoice invoice = new Invoice();
        invoice.setStatus(invoiceCreate.getStatus());
        invoice.setAmount(invoiceCreate.getAmount());
        invoice.setCustomer(customer);
        return invoice;
    }

    @Override
    public Invoice toModel(InvoiceUpdate invoiceUpdate, Customer customer) {
        Invoice invoice = new Invoice();
        invoice.set_id(new ObjectId(invoiceUpdate.getInvoice_id()));
        invoice.setStatus(invoiceUpdate.getStatus());
        invoice.setAmount(invoiceUpdate.getAmount());
        invoice.setCustomer(customer);
        return invoice;
    }
}