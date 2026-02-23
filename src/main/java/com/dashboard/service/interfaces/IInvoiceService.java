package com.dashboard.service.interfaces;

import com.dashboard.dataTransferObject.invoice.InvoiceCreate;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.dataTransferObject.invoice.InvoiceUpdate;
import com.dashboard.model.entities.Invoice;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface IInvoiceService {
    List<Invoice> getAllInvoices();
    List<Invoice> getInvoicesByStatus(String status);
    List<Invoice> getLatestInvoice(Integer indexFrom, Integer indexTo);
    Page<Invoice> searchInvoices(String rawTerm, Pageable pageable);
    Invoice getInvoiceById(String id);
    InvoiceRead createInvoice(InvoiceCreate invoiceCreate);
    InvoiceRead updateInvoice(String id, InvoiceUpdate invoiceUpdate);
    void deleteInvoice(String id);
}