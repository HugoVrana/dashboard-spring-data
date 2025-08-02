package com.dashboard.service;

import com.dashboard.model.Invoice;
import com.dashboard.repository.IInvoiceRepository;
import com.dashboard.service.interfaces.IInvoiceService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;

@Service
@Scope("singleton")
public class InvoiceService implements IInvoiceService {
    private final IInvoiceRepository invoiceRepository;

    public InvoiceService(IInvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public  List<Invoice> getInvoicesByStatus(String status){
        return invoiceRepository.findByStatus(status);
    }

    public List<Invoice> getLatestInvoice(Integer indexFrom, Integer indexTo) {
        return invoiceRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Invoice::getDate).reversed()) // latest first
                .skip(indexFrom)
                .limit(indexTo - indexFrom + 1)
                .toList();
    }
}
