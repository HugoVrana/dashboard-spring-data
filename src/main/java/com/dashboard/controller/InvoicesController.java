package com.dashboard.controller;

import com.dashboard.dataTransferObjects.InvoiceDto;
import com.dashboard.mappers.CustomerMapper;
import com.dashboard.mappers.InvoiceMapper;
import com.dashboard.model.Invoice;
import com.dashboard.service.InvoiceService;
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
    public List<InvoiceDto> getAllInvoices() {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        return mapToDtos(invoices);
    }

    @GetMapping("/latest")
    public List<InvoiceDto> getLatestInvoice(@RequestParam(required = false) Integer indexFrom, @RequestParam(required = false) Integer indexTo) {
        List<Invoice> invoices = (indexFrom == null || indexTo == null)
                ? invoiceService.getAllInvoices()
                : invoiceService.getLatestInvoice(indexFrom, indexTo);
        return mapToDtos(invoices);
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
    public List<InvoiceDto> searchInvoices(@RequestParam String searchTerm) {
        List<Invoice> invoices =  invoiceService.searchInvoices(searchTerm);
        return mapToDtos(invoices);
    }

    @org.jetbrains.annotations.NotNull
    private List<InvoiceDto> mapToDtos(List<Invoice> invoices) {
        List<InvoiceDto> invoiceDtos = new ArrayList<>();
        for(Invoice invoice : invoices) {
            InvoiceDto invoiceDto = invoiceMapper.toDto(invoice);
            invoiceDto.setCustomer(customerMapper.toDto(invoice.getCustomer()));
            invoiceDtos.add(invoiceDto);
        }
        return invoiceDtos;
    }
}