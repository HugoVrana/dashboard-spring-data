package com.dashboard.service.interfaces;

import com.dashboard.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IInvoiceService {
    List<Invoice> getAllInvoices();
    List<Invoice> getInvoicesByStatus(String status);
    List<Invoice> getLatestInvoice(Integer indexFrom, Integer indexTo);
    Page<Invoice> searchInvoices(String rawTerm, Pageable pageable);
    Invoice insertInvoice(Invoice invoice);
}