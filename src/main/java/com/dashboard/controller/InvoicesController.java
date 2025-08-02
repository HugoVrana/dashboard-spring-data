package com.dashboard.controller;

import com.dashboard.dataTransferObjects.InvoiceDto;
import com.dashboard.model.Invoice;
import com.dashboard.service.interfaces.IInvoiceService;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
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

    @GetMapping("/latest")
    public List<InvoiceDto> getLatestInvoice(@RequestParam(required = false) Integer indexFrom, @RequestParam(required = false) Integer indexTo) {
        List<Invoice> invoices = (indexFrom == null || indexTo == null)
                ? invoiceService.getAllInvoices()
                : invoiceService.getLatestInvoice(indexFrom, indexTo);

        List<InvoiceDto> invoiceDtos = new ArrayList<>();
        for(Invoice invoice : invoices){
            InvoiceDto invoiceDto = new InvoiceDto(invoice);
            invoiceDtos.add(invoiceDto);
        }
        return invoiceDtos;
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
