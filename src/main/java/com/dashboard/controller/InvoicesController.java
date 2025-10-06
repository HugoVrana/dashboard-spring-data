package com.dashboard.controller;

import com.dashboard.dataTransferObject.page.PageRead;
import com.dashboard.dataTransferObject.page.PageRequest;
import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.dataTransferObject.invoice.InvoiceCreate;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.mapper.interfaces.ICustomerMapper;
import com.dashboard.mapper.interfaces.IInvoiceMapper;
import com.dashboard.model.Invoice;
import com.dashboard.model.exception.NotFoundException;
import com.dashboard.service.interfaces.ICustomerService;
import com.dashboard.service.interfaces.IInvoiceService;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/invoices")
public class InvoicesController {

    private final IInvoiceService invoiceService;
    private final ICustomerService customersService;
    private final IInvoiceMapper invoiceMapper;
    private final ICustomerMapper customerMapper;

    public InvoicesController(IInvoiceService invoiceService,
                              ICustomerService customersService,
                              IInvoiceMapper invoiceMapper,
                              ICustomerMapper customerMapper) {
        this.invoiceService = invoiceService;
        this.customersService = customersService;
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

    @GetMapping("/pages")
    public Integer getPages(@RequestParam(required = false) String searchTerm, @RequestParam(required = false) Integer size) {
        if (size == null || size < 1) {
            size = 15;
        }
        Page<Invoice> invoices = invoiceService.searchInvoices(searchTerm, Pageable.ofSize(size));
        return invoices.getTotalPages();
    }

    @PostMapping(value = "/search", consumes = "application/json")
    public PageRead<InvoiceRead> searchInvoices(@RequestBody PageRequest pageRequest) {
        Pageable pageable;
        if (pageRequest.getPage() == null || pageRequest.getPage() < 1) {
            pageable = Pageable.unpaged();
        }
        else {
            pageable = Pageable.ofSize(pageRequest.getSize()).withPage(pageRequest.getPage() - 1);
        }

        Page<Invoice> invoices =  invoiceService.searchInvoices(pageRequest.getSearch(), pageable); // always returns all invoices

        PageRead<InvoiceRead> pageRead = new PageRead<>();
        List<Invoice> content = invoices.stream().toList();
        List<InvoiceRead> invoiceReads = new ArrayList<>();
        for(Invoice invoice : content) {
            InvoiceRead invoiceRead = invoiceMapper.toRead(invoice);
            CustomerRead customerRead = customerMapper.toRead(invoice.getCustomer());
            invoiceRead.setCustomer(customerRead);
            invoiceReads.add(invoiceRead);
        }

        pageRead.setData(invoiceReads);
        pageRead.setTotalPages(invoices.getTotalPages());
        pageRead.setItemsPerPage(invoices.getSize());
        pageRead.setCurrentPage(invoices.getNumber() + 1);
        return pageRead;
    }

    @PostMapping()
    public ResponseEntity<InvoiceRead> createInvoice(@Valid @RequestBody InvoiceCreate invoiceCreate) {
        // At this point, customerId is present and matches ObjectId pattern.
        var customerId = new ObjectId(invoiceCreate.getCustomer_id());

        var customer = customersService.getCustomer(customerId)
                .orElseThrow(() -> new NotFoundException("The provided customer id does not exist"));

        var invoice = invoiceMapper.toModel(invoiceCreate, customer);
        invoice.setDate(LocalDate.now());

        invoice = invoiceService.insertInvoice(invoice);// save returns entity with id populated
        var invoiceRead = invoiceMapper.toRead(invoice);
        invoiceRead.setCustomer(customerMapper.toRead(customer));

        // Build a Location like /invoices/{id}
        var location = URI.create("/invoices/" + invoice.get_id());
        return ResponseEntity.created(location).body(invoiceRead);
    }
}