package com.dashboard.service.interfaces;

import com.dashboard.model.Invoice;
import java.util.List;

public interface IInvoiceService {
    List<Invoice> getAllInvoices();
    List<Invoice> getInvoicesByStatus(String status);
    List<Invoice> getLatestInvoice(Integer indexFrom, Integer indexTo);
}
