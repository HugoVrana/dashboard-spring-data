package com.dashboard.service;

import com.dashboard.repository.IInvoiceRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class InvoiceService {
    private final IInvoiceRepository invoiceRepository;

    public InvoiceService(IInvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }
}
