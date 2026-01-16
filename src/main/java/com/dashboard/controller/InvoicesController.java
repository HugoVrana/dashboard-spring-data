package com.dashboard.controller;

import com.dashboard.common.model.Audit;
import com.dashboard.common.model.exception.NotFoundException;
import com.dashboard.common.model.exception.ResourceNotFoundException;
import com.dashboard.dataTransferObject.invoice.InvoiceCreate;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.dataTransferObject.invoice.InvoiceUpdate;
import com.dashboard.dataTransferObject.page.PageRead;
import com.dashboard.dataTransferObject.page.PageRequest;
import com.dashboard.mapper.interfaces.ICustomerMapper;
import com.dashboard.mapper.interfaces.IInvoiceMapper;
import com.dashboard.mapper.interfaces.IInvoiceSearchMapper;
import com.dashboard.model.entities.Customer;
import com.dashboard.model.entities.Invoice;
import com.dashboard.model.entities.InvoiceSearchDocument;
import com.dashboard.service.interfaces.ICustomerService;
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
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoicesController {

    private final IInvoiceService invoiceService;
    private final IInvoiceSearchService invoiceSearchService;
    private final ICustomerService customersService;
    private final IInvoiceMapper invoiceMapper;
    private final ICustomerMapper customerMapper;
    private final IInvoiceSearchMapper invoiceSearchMapper;

    @GetMapping("/")
    @PreAuthorize("hasAuthority('dashboard-invoices-read')")
    public ResponseEntity<List<InvoiceRead>> getAllInvoices() {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        List<InvoiceRead> invoiceReads = new ArrayList<>();
        for (Invoice invoice : invoices) {
            InvoiceRead invoiceRead = invoiceMapper.toRead(invoice);
            invoiceRead.setCustomer(customerMapper.toRead(invoice.getCustomer()));
            invoiceReads.add(invoiceRead);
        }
        return ResponseEntity.ok(invoiceReads);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard-invoices-read')")
    public ResponseEntity<InvoiceRead> getInvoiceById(@PathVariable("id") String id) {

        if (!ObjectId.isValid(id)) {
            throw new ResourceNotFoundException("This id is invalid");
        }

        ObjectId invoiceId = new ObjectId(id);
        Optional<Invoice> optionalInvoice = invoiceService.getInvoiceById(invoiceId);
        if (optionalInvoice.isEmpty()) {
            throw new ResourceNotFoundException("Invoice with id " + id + " not found");
        }

        Invoice invoice = optionalInvoice.get();
        InvoiceRead invoiceRead = invoiceMapper.toRead(invoice);
        invoiceRead.setCustomer(customerMapper.toRead(invoice.getCustomer()));
        return ResponseEntity.ok(invoiceRead);
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
        List<InvoiceRead> invoiceReads = new ArrayList<>();
        for (Invoice invoice : invoices) {
            InvoiceRead invoiceRead = invoiceMapper.toRead(invoice);
            invoiceRead.setCustomer(customerMapper.toRead(invoice.getCustomer()));
            invoiceReads.add(invoiceRead);
        }
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
        Integer cout = invoices.size();
        return ResponseEntity.ok(cout);
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
            invoiceReads.add(invoiceSearchMapper.toRead(doc));
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
        // At this point, customerId is present and matches ObjectId pattern.
        ObjectId customerId = new ObjectId(invoiceCreate.getCustomer_id());

        Customer customer = customersService.getCustomer(customerId)
                .orElseThrow(() -> new NotFoundException("The provided customer id does not exist"));

        Instant now = Instant.now();

        Audit audit = new Audit();
        audit.setCreatedAt(now);
        audit.setUpdatedAt(now);

        Invoice invoice = invoiceMapper.toModel(invoiceCreate, customer);
        invoice.setDate(LocalDate.now());
        invoice.setAudit(audit);
        invoice = invoiceService.insertInvoice(invoice);// save returns entity with id populated
        InvoiceRead invoiceRead = invoiceMapper.toRead(invoice);
        invoiceRead.setCustomer(customerMapper.toRead(customer));

        // Build a Location like /invoices/{id}
        URI location = URI.create("/invoices/" + invoice.get_id());
        return ResponseEntity.created(location).body(invoiceRead);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard-invoices-update')")
    public ResponseEntity<InvoiceRead> updateInvoice(@PathVariable("id") String id, @Valid @RequestBody InvoiceUpdate invoiceUpdate) {
        if (!ObjectId.isValid(id)) {
            throw new ResourceNotFoundException("This id is invalid");
        }
        ObjectId invoiceId = new ObjectId(id);

        Optional<Invoice> unupdatedOptionalInvoice = invoiceService.getInvoiceById(invoiceId);
        if (unupdatedOptionalInvoice.isEmpty()) {
            throw new ResourceNotFoundException("Invoice with id " + id + " not found");
        }

        ObjectId customerId = new ObjectId(invoiceUpdate.getCustomer_id());
        Optional<Customer> optionalCustomer = customersService.getCustomer(customerId);
        if (optionalCustomer.isEmpty()) {
            throw new ResourceNotFoundException("Customer with id " + customerId + " not found");
        }

        Invoice unupdatedInvoice = unupdatedOptionalInvoice.get();
        Audit newAudit = unupdatedInvoice.getAudit();
        newAudit.setUpdatedAt(Instant.now());

        Customer customer = optionalCustomer.get();
        Invoice invoice = invoiceMapper.toModel(invoiceUpdate, customer);
        invoice.setDate(unupdatedInvoice.getDate());
        invoice.setAudit(newAudit);

        invoice = invoiceService.updateInvoice(invoice);

        InvoiceRead invoiceRead = invoiceMapper.toRead(invoice);
        invoiceRead.setCustomer(customerMapper.toRead(customer));

        // Build a Location like /invoices/{id}
        URI location = URI.create("/invoices/" + invoice.get_id());
        return ResponseEntity.created(location).body(invoiceRead);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard-invoices-delete')")
    public ResponseEntity<Integer> deleteInvoice(@PathVariable("id") String id) {
        if (!ObjectId.isValid(id)) {
            throw new ResourceNotFoundException("This id is invalid");
        }
        ObjectId invoiceId = new ObjectId(id);
        Optional<Invoice> optionalInvoice = invoiceService.getInvoiceById(invoiceId);
        if (optionalInvoice.isEmpty()) {
            throw new ResourceNotFoundException("Invoice with id " + id + " not found");
        }

        Invoice invoice = optionalInvoice.get();
        Audit audit = invoice.getAudit();
        audit.setDeletedAt(Instant.now());
        invoice.setAudit(audit);
        invoiceService.updateInvoice(invoice);
        invoiceSearchService.markInvoiceDeleted(invoiceId);

        optionalInvoice = invoiceService.getInvoiceById(invoiceId);
        if (optionalInvoice.isPresent()) {
            throw new ResourceNotFoundException("Invoice with id " + id + " not deleted");
        }
        return ResponseEntity.ok(1);
    }
}