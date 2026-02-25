package com.dashboard.service;

import com.dashboard.model.entities.Customer;
import com.dashboard.model.entities.Invoice;
import com.dashboard.model.entities.InvoiceSearchDocument;
import com.dashboard.repository.IInvoiceRepository;
import com.dashboard.repository.IInvoiceSearchRepository;
import com.dashboard.service.interfaces.IInvoiceSearchService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Scope("singleton")
@RequiredArgsConstructor
public class InvoiceSearchService implements IInvoiceSearchService {

    private final IInvoiceSearchRepository invoiceSearchRepository;
    private final IInvoiceRepository invoiceRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<InvoiceSearchDocument> search(String searchTerm, Pageable pageable) {
        // Empty search - return all non-deleted documents
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return invoiceSearchRepository.findByDeletedAtIsNull(pageable);
        }

        String term = searchTerm.trim();

        // If it's a valid ObjectId, search by invoice ID or customer ID
        if (ObjectId.isValid(term)) {
            ObjectId objectId = new ObjectId(term);
            Query q = new Query()
                    .addCriteria(new Criteria().orOperator(
                            Criteria.where("invoiceId").is(objectId),
                            Criteria.where("customerId").is(objectId)
                    ))
                    .addCriteria(Criteria.where("deletedAt").is(null))
                    .with(pageable);

            List<InvoiceSearchDocument> results = mongoTemplate.find(q, InvoiceSearchDocument.class);
            long count = mongoTemplate.count(Query.query(new Criteria().orOperator(
                    Criteria.where("invoiceId").is(objectId),
                    Criteria.where("customerId").is(objectId)
            )).addCriteria(Criteria.where("deletedAt").is(null)), InvoiceSearchDocument.class);
            return new PageImpl<>(results, pageable, count);
        }

        // Use regex for partial text matching (more flexible than MongoDB text search)
        String regex = Pattern.quote(term);
        List<Criteria> searchCriteria = new ArrayList<>();

        // Text fields - case insensitive regex
        searchCriteria.add(Criteria.where("status").regex(regex, "i"));
        searchCriteria.add(Criteria.where("customerName").regex(regex, "i"));
        searchCriteria.add(Criteria.where("customerEmail").regex(regex, "i"));

        // Numeric search for amount
        try {
            BigDecimal numericValue = new BigDecimal(term);
            searchCriteria.add(Criteria.where("amount").is(numericValue));
        } catch (NumberFormatException ignored) {
            // Not a number, skip
        }

        Query query = new Query()
                .addCriteria(new Criteria().orOperator(searchCriteria.toArray(new Criteria[0])))
                .addCriteria(Criteria.where("deletedAt").is(null))
                .with(pageable);

        List<InvoiceSearchDocument> results = mongoTemplate.find(query, InvoiceSearchDocument.class);

        // Count query without pagination
        Query countQuery = new Query()
                .addCriteria(new Criteria().orOperator(searchCriteria.toArray(new Criteria[0])))
                .addCriteria(Criteria.where("deletedAt").is(null));
        long total = mongoTemplate.count(countQuery, InvoiceSearchDocument.class);

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public void syncInvoice(Invoice invoice) {
        if (invoice == null || invoice.get_id() == null) {
            return;
        }

        Customer customer = invoice.getCustomer();
        if (customer == null) {
            return;
        }

        Optional<InvoiceSearchDocument> existingDoc = invoiceSearchRepository
                .findByInvoiceIdAndDeletedAtIsNull(invoice.get_id());

        InvoiceSearchDocument doc;
        if (existingDoc.isPresent()) {
            doc = existingDoc.get();
        } else {
            doc = new InvoiceSearchDocument();
            doc.setInvoiceId(invoice.get_id());
        }

        // Update invoice fields
        doc.setAmount(invoice.getAmount());
        doc.setDate(invoice.getDate());
        doc.setStatus(invoice.getStatus());

        // Update customer fields
        doc.setCustomerId(customer.get_id());
        doc.setCustomerName(customer.getName());
        doc.setCustomerEmail(customer.getEmail());
        doc.setCustomerImageUrl(customer.getImageUrl());

        // Update tracking
        doc.setLastSyncedAt(Instant.now());

        invoiceSearchRepository.save(doc);
    }

    @Override
    public void syncCustomer(Customer customer) {
        if (customer == null || customer.get_id() == null) {
            return;
        }

        // Batch update all search documents for this customer
        Query query = Query.query(Criteria.where("customerId").is(customer.get_id()));
        Update update = new Update()
                .set("customerName", customer.getName())
                .set("customerEmail", customer.getEmail())
                .set("customerImageUrl", customer.getImageUrl())
                .set("lastSyncedAt", Instant.now());

        mongoTemplate.updateMulti(query, update, InvoiceSearchDocument.class);
    }

    @Override
    public void markInvoiceDeleted(ObjectId invoiceId) {
        if (invoiceId == null) {
            return;
        }

        Query query = Query.query(Criteria.where("invoiceId").is(invoiceId));
        Update update = new Update().set("deletedAt", Instant.now());

        mongoTemplate.updateFirst(query, update, InvoiceSearchDocument.class);
    }

    @Override
    public void rebuildIndex() {
        // Clear all existing search documents
        mongoTemplate.remove(new Query(), InvoiceSearchDocument.class);

        // Get all non-deleted invoices and sync them
        List<Invoice> allInvoices = invoiceRepository.findByAudit_DeletedAtIsNull();
        for (Invoice invoice : allInvoices) {
            syncInvoice(invoice);
        }
    }
}
