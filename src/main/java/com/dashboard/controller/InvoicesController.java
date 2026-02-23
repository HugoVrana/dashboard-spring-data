package com.dashboard.controller;

import com.dashboard.dataTransferObject.invoice.InvoiceCreate;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.dataTransferObject.invoice.InvoiceUpdate;
import com.dashboard.dataTransferObject.page.PageRead;
import com.dashboard.dataTransferObject.page.PageRequest;
import com.dashboard.mapper.interfaces.IInvoiceMapper;
import com.dashboard.mapper.interfaces.IInvoiceSearchMapper;
import com.dashboard.model.entities.Invoice;
import com.dashboard.model.entities.InvoiceSearchDocument;
import com.dashboard.service.interfaces.IInvoiceSearchService;
import com.dashboard.service.interfaces.IInvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoicesController {

    private final IInvoiceService invoiceService;
    private final IInvoiceSearchService invoiceSearchService;
    private final IInvoiceMapper invoiceMapper;
    private final IInvoiceSearchMapper invoiceSearchMapper;

    @GetMapping("/")
    @PreAuthorize("hasAuthority('dashboard-invoices-read')")
    public ResponseEntity<List<InvoiceRead>> getAllInvoices() {
        List<InvoiceRead> invoiceReads = invoiceService.getAllInvoices().stream()
                .map(invoiceMapper::toReadWithCustomer)
                .toList();
        return ResponseEntity.ok(invoiceReads);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard-invoices-read')")
    public ResponseEntity<InvoiceRead> getInvoiceById(@PathVariable("id") String id) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(invoiceMapper.toReadWithCustomer(invoice));
    }

    @GetMapping("/latest")
    @PreAuthorize("hasAuthority('dashboard-invoices-read')")
    public ResponseEntity<List<InvoiceRead>> getLatestInvoice(@RequestParam(required = false) Integer indexFrom, @RequestParam(required = false) Integer indexTo) {
        if (indexFrom != null && indexTo != null && indexFrom > indexTo) {
            throw new IllegalArgumentException("indexFrom must be less or equal to indexTo");
        }

        List<Invoice> invoices = (indexFrom == null || indexTo == null)
                ? invoiceService.getAllInvoices()
                : invoiceService.getLatestInvoice(indexFrom, indexTo);
        List<InvoiceRead> invoiceReads = invoices.stream()
                .map(invoiceMapper::toReadWithCustomer)
                .toList();
        return ResponseEntity.ok(invoiceReads);
    }

    @GetMapping("/count")
    @PreAuthorize("hasAuthority('dashboard-invoices-read')")
    public ResponseEntity<Integer> getInvoiceCount(@RequestParam(required = false) String status) {
        List<Invoice> invoices;
        if (status == null) {
            invoices = invoiceService.getAllInvoices();
        } else {
            invoices = invoiceService.getInvoicesByStatus(status);
        }
        Integer count = invoices.size();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/amount")
    @PreAuthorize("hasAuthority('dashboard-invoices-read')")
    public ResponseEntity<Double> getInvoiceAmount(@RequestParam(required = false) String status) {
        double amount;
        if (status == null) {
            amount = invoiceService.getAllInvoices()
                    .stream()
                    .mapToDouble(Invoice::getAmount)
                    .sum();
        } else {
            amount = invoiceService.getInvoicesByStatus(status)
                    .stream()
                    .mapToDouble(Invoice::getAmount)
                    .sum();
        }
        return ResponseEntity.ok(amount);
    }

    @GetMapping("/pages")
    @PreAuthorize("hasAuthority('dashboard-invoices-read')")
    public ResponseEntity<Integer> getPages(@RequestParam(required = false) String searchTerm, @RequestParam(required = false) Integer size) {
        if (size == null || size < 1) {
            size = 15;
        }
        Page<InvoiceSearchDocument> invoices = invoiceSearchService.search(searchTerm, Pageable.ofSize(size));
        Integer pages = invoices.getTotalPages();
        return ResponseEntity.ok(pages);
    }

    @PostMapping(value = "/search", consumes = "application/json")
    @PreAuthorize("hasAuthority('dashboard-invoices-read')")
    public ResponseEntity<PageRead<InvoiceRead>> searchInvoices(@Valid @RequestBody PageRequest pageRequest) {
        if (pageRequest.getPage() != null && pageRequest.getPage() <= 0) {
            throw new IllegalArgumentException("Page number must be greater than 0");
        }

        Pageable pageable;
        if (pageRequest.getPage() == null) {
            pageable = Pageable.unpaged();
        } else {
            pageable = Pageable
                    .ofSize(pageRequest.getSize())
                    .withPage(pageRequest.getPage() - 1);
        }
        Page<InvoiceSearchDocument> searchResults = invoiceSearchService.search(pageRequest.getSearch(), pageable);

        if (searchResults.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        PageRead<InvoiceRead> pageRead = new PageRead<>();
        List<InvoiceRead> invoiceReads = new ArrayList<>();
        for (InvoiceSearchDocument doc : searchResults.getContent()) {
            InvoiceRead ir = invoiceSearchMapper.toRead(doc);
            invoiceReads.add(ir);
        }

        pageRead.setData(invoiceReads);
        pageRead.setTotalPages(searchResults.getTotalPages());
        pageRead.setItemsPerPage(searchResults.getSize());
        pageRead.setCurrentPage(searchResults.getNumber() + 1);
        return ResponseEntity.ok(pageRead);
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('dashboard-invoices-create')")
    public ResponseEntity<InvoiceRead> createInvoice(@Valid @RequestBody InvoiceCreate invoiceCreate) {
        InvoiceRead invoiceRead = invoiceService.createInvoice(invoiceCreate);
        URI location = URI.create("/invoices/" + invoiceRead.getId());
        return ResponseEntity.created(location).body(invoiceRead);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard-invoices-update')")
    public ResponseEntity<InvoiceRead> updateInvoice(@PathVariable("id") String id, @Valid @RequestBody InvoiceUpdate invoiceUpdate) {
        InvoiceRead invoiceRead = invoiceService.updateInvoice(id, invoiceUpdate);
        URI location = URI.create("/invoices/" + invoiceRead.getId());
        return ResponseEntity.created(location).body(invoiceRead);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard-invoices-delete')")
    public ResponseEntity<Integer> deleteInvoice(@PathVariable("id") String id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok(1);
    }
}