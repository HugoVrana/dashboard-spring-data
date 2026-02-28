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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@Tag(name = "Invoices", description = "Invoice management operations")
@RequestMapping(value = "/invoices", produces = "application/json")
@RequiredArgsConstructor
public class InvoicesController {

    private final IInvoiceService invoiceService;
    private final IInvoiceSearchService invoiceSearchService;
    private final IInvoiceMapper invoiceMapper;
    private final IInvoiceSearchMapper invoiceSearchMapper;

    @Operation(summary = "Get all invoices", description = "Retrieves a list of all invoices")
    @GetMapping("/")
    @PreAuthorize("hasAuthority('dashboard-invoices-read')")
    public ResponseEntity<List<InvoiceRead>> getAllInvoices() {
        List<InvoiceRead> invoiceReads = invoiceService.getAllInvoices().stream()
                .map(invoiceMapper::toReadWithCustomer)
                .toList();
        return ResponseEntity.ok(invoiceReads);
    }

    @Operation(summary = "Get invoice by ID", description = "Retrieves a specific invoice by its ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard-invoices-read')")
    public ResponseEntity<InvoiceRead> getInvoiceById(@Parameter(description = "Invoice ID") @PathVariable("id") String id) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(invoiceMapper.toReadWithCustomer(invoice));
    }

    @Operation(summary = "Get latest invoices", description = "Retrieves the most recent invoices with optional range parameters")
    @GetMapping("/latest")
    @PreAuthorize("hasAuthority('dashboard-invoices-read')")
    public ResponseEntity<List<InvoiceRead>> getLatestInvoice(
            @Parameter(description = "Starting index") @RequestParam(required = false) Integer indexFrom,
            @Parameter(description = "Ending index") @RequestParam(required = false) Integer indexTo) {
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

    @Operation(summary = "Get invoice count", description = "Returns the total number of invoices, optionally filtered by status")
    @GetMapping("/count")
    @PreAuthorize("hasAuthority('dashboard-invoices-read')")
    public ResponseEntity<Integer> getInvoiceCount(@Parameter(description = "Filter by invoice status") @RequestParam(required = false) String status) {
        List<Invoice> invoices;
        if (status == null) {
            invoices = invoiceService.getAllInvoices();
        } else {
            invoices = invoiceService.getInvoicesByStatus(status);
        }
        Integer count = invoices.size();
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Get total invoice amount", description = "Returns the sum of all invoice amounts, optionally filtered by status")
    @GetMapping("/amount")
    @PreAuthorize("hasAuthority('dashboard-invoices-read')")
    public ResponseEntity<BigDecimal> getInvoiceAmount(@Parameter(description = "Filter by invoice status") @RequestParam(required = false) String status) {
        List<Invoice> invoices = (status == null)
                ? invoiceService.getAllInvoices()
                : invoiceService.getInvoicesByStatus(status);
        BigDecimal amount = invoices.stream()
                .map(Invoice::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(amount);
    }

    @Operation(summary = "Get page count", description = "Returns the total number of pages for search results")
    @GetMapping("/pages")
    @PreAuthorize("hasAuthority('dashboard-invoices-read')")
    public ResponseEntity<Integer> getPages(
            @Parameter(description = "Search term to filter invoices") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Page size") @RequestParam(required = false) Integer size) {
        if (size == null || size < 1) {
            size = 15;
        }
        Page<InvoiceSearchDocument> invoices = invoiceSearchService.search(searchTerm, Pageable.ofSize(size));
        Integer pages = invoices.getTotalPages();
        return ResponseEntity.ok(pages);
    }

    @Operation(summary = "Search invoices", description = "Searches invoices with pagination support")
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

    @Operation(summary = "Create invoice", description = "Creates a new invoice")
    @PostMapping()
    @PreAuthorize("hasAuthority('dashboard-invoices-create')")
    public ResponseEntity<InvoiceRead> createInvoice(@Valid @RequestBody InvoiceCreate invoiceCreate) {
        InvoiceRead invoiceRead = invoiceService.createInvoice(invoiceCreate);
        URI location = URI.create("/invoices/" + invoiceRead.getId());
        return ResponseEntity.created(location).body(invoiceRead);
    }

    @Operation(summary = "Update invoice", description = "Updates an existing invoice")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard-invoices-update')")
    public ResponseEntity<InvoiceRead> updateInvoice(@Parameter(description = "Invoice ID") @PathVariable("id") String id, @Valid @RequestBody InvoiceUpdate invoiceUpdate) {
        InvoiceRead invoiceRead = invoiceService.updateInvoice(id, invoiceUpdate);
        URI location = URI.create("/invoices/" + invoiceRead.getId());
        return ResponseEntity.created(location).body(invoiceRead);
    }

    @Operation(summary = "Delete invoice", description = "Soft deletes an invoice by its ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard-invoices-delete')")
    public ResponseEntity<Integer> deleteInvoice(@Parameter(description = "Invoice ID") @PathVariable("id") String id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok(1);
    }
}