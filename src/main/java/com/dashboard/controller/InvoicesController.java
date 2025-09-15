package com.dashboard.controller;

import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.mapper.CustomerMapper;
import com.dashboard.mapper.InvoiceMapper;
import com.dashboard.model.Invoice;
import com.dashboard.service.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/invoices")
public class InvoicesController {
    private final InvoiceService invoiceService;
    private final InvoiceMapper invoiceMapper;
    private final CustomerMapper customerMapper;

    public InvoicesController(InvoiceService invoiceService, InvoiceMapper invoiceMapper, CustomerMapper customerMapper) {
        this.invoiceService = invoiceService;
        this.invoiceMapper = invoiceMapper;
        this.customerMapper = customerMapper;
    }

    @GetMapping("/")
    public List<InvoiceRead> getAllInvoices() {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        List<InvoiceRead> invoiceReads = new ArrayList<>();
        for(Invoice invoice : invoices) {
            InvoiceRead invoiceRead = invoiceMapper.toRead(invoice);
            invoiceRead.setCustomer(customerMapper.toRead(invoice.getCustomer()));
            invoiceReads.add(invoiceRead);
        }
        return invoiceReads;
    }

    @GetMapping("/latest")
    public List<InvoiceRead> getLatestInvoice(@RequestParam(required = false) Integer indexFrom, @RequestParam(required = false) Integer indexTo) {
        List<Invoice> invoices = (indexFrom == null || indexTo == null)
                ? invoiceService.getAllInvoices()
                : invoiceService.getLatestInvoice(indexFrom, indexTo);
        List<InvoiceRead> invoiceReads = new ArrayList<>();
        for(Invoice invoice : invoices) {
            InvoiceRead invoiceRead = invoiceMapper.toRead(invoice);
            invoiceRead.setCustomer(customerMapper.toRead(invoice.getCustomer()));
            invoiceReads.add(invoiceRead);
        }
        return invoiceReads;
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

    @GetMapping("/search")
    public List<InvoiceRead> searchInvoices(@RequestParam String searchTerm) {
        Pageable pageable = Pageable.unpaged();
        Page<Invoice> invoices =  invoiceService.searchInvoices(searchTerm, pageable);
        List<Invoice> content = invoices.stream().toList();
        List<InvoiceRead> invoiceReads = new ArrayList<>();
        for(Invoice invoice : content) {
            InvoiceRead invoiceRead = invoiceMapper.toRead(invoice);
            CustomerRead customerRead = customerMapper.toRead(invoice.getCustomer());
            invoiceRead.setCustomer(customerRead);
            invoiceReads.add(invoiceRead);
        }
        return invoiceReads;
    }
}