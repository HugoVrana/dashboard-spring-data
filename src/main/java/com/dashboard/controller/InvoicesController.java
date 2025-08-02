package com.dashboard.controller;

import com.dashboard.model.Invoice;
import com.dashboard.service.interfaces.IInvoiceService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/invoices")
public class InvoicesController {

    private final IInvoiceService invoiceService;

    public InvoicesController(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/")
    public List<Invoice> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }
}
