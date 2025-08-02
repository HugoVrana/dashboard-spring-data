package com.dashboard.controller;

import com.dashboard.model.Invoice;
import com.dashboard.service.interfaces.IInvoiceService;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/count")
    public Integer getInvoiceCount(@RequestParam(required = false) String status) {
        if (status == null) {
            return invoiceService.getAllInvoices().size();
        }
        return invoiceService.getInvoicesByStatus(status).size();
    }

    @GetMapping("/amount")
    public Double getInvoiceAmount(@RequestParam(required = false) String status) {
        if (status == null) {
            return invoiceService.getAllInvoices()
                    .stream()
                    .mapToDouble(Invoice::getAmount)
                    .sum();
        }
        return invoiceService.getInvoicesByStatus(status)
                .stream()
                .mapToDouble(Invoice::getAmount)
                .sum();
    }
}
