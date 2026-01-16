package com.dashboard.service;

import com.dashboard.model.entities.Invoice;
import com.dashboard.repository.IInvoiceRepository;
import com.dashboard.service.interfaces.IInvoiceSearchService;
import com.dashboard.service.interfaces.IInvoiceService;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Scope("singleton")
@RequiredArgsConstructor
public class InvoiceService implements IInvoiceService {
    private final IInvoiceRepository invoiceRepository;
    private final MongoTemplate mongoTemplate;
    private final IInvoiceSearchService invoiceSearchService;

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findByAudit_DeletedAtIsNull();
    }

    public  List<Invoice> getInvoicesByStatus(String status){
        return invoiceRepository.findByStatusAndAudit_DeletedAtIsNull(status);
    }

    public List<Invoice> getLatestInvoice(Integer indexFrom, Integer indexTo) {
        return invoiceRepository.findByAudit_DeletedAtIsNull()
                .stream()
                .sorted(Comparator.comparing(Invoice::getDate).reversed()) // latest first
                .skip(indexFrom)
                .limit(indexTo - indexFrom + 1)
                .toList();
    }

    public Page<Invoice> searchInvoices(String rawTerm, Pageable pageable) {
        // Empty search — return all non-deleted invoices
        if (rawTerm == null || rawTerm.trim().isEmpty()) {
            Query q = new Query()
                    .addCriteria(Criteria.where("audit.deletedAt").is(null))
                    .with(pageable);

            List<Invoice> results = mongoTemplate.find(q, Invoice.class);
            long count = mongoTemplate.count(Query.query(Criteria.where("audit.deletedAt").is(null)), Invoice.class);
            return new PageImpl<>(results, pageable, count);
        }

        String term = rawTerm.trim();

        // If it's a valid ObjectId, search by ID directly
        if (ObjectId.isValid(term)) {
            Query q = new Query()
                    .addCriteria(new Criteria().orOperator(
                            Criteria.where("_id").is(new ObjectId(term)),
                            Criteria.where("customer.$id").is(new ObjectId(term))
                    ))
                    .addCriteria(Criteria.where("audit.deletedAt").is(null));

            List<Invoice> results = mongoTemplate.find(q, Invoice.class);
            return new PageImpl<>(results, pageable, results.size());
        }

        // Use $lookup + regex for text search (no Atlas Search with DBRef)
        List<AggregationOperation> operations = new ArrayList<>();

        // First: filter out soft-deleted invoices (do this early to reduce data)
        operations.add(Aggregation.match(Criteria.where("audit.deletedAt").is(null)));

        // $lookup to join customer data
        operations.add(context -> new Document("$lookup", new Document()
                .append("from", "customers")
                .append("localField", "customer.$id")
                .append("foreignField", "_id")
                .append("as", "customerData")
        ));

        // Unwind the customer array
        operations.add(Aggregation.unwind("customerData"));

        // Filter soft-deleted customers
        operations.add(Aggregation.match(Criteria.where("customerData.audit.deletedAt").is(null)));

        // Build search criteria
        String regex = Pattern.quote(term);
        List<Criteria> searchCriteria = new ArrayList<>();

        // Text fields - case insensitive regex
        searchCriteria.add(Criteria.where("status").regex(regex, "i"));
        searchCriteria.add(Criteria.where("customerData.name").regex(regex, "i"));
        searchCriteria.add(Criteria.where("customerData.email").regex(regex, "i"));

        // Numeric search for amount
        try {
            double numericValue = Double.parseDouble(term);
            searchCriteria.add(Criteria.where("amount").is(numericValue));
        } catch (NumberFormatException ignored) {
            // Not a number, skip
        }

        operations.add(Aggregation.match(new Criteria().orOperator(
                searchCriteria.toArray(new Criteria[0])
        )));

        // Facet for pagination + total count
        int skip = pageable.isUnpaged() ? 0 : (int) pageable.getOffset();
        int limit = pageable.isUnpaged() ? Integer.MAX_VALUE : pageable.getPageSize();

        Document facetStage = new Document("$facet", new Document()
                .append("results", List.of(
                        new Document("$skip", skip),
                        new Document("$limit", limit)
                ))
                .append("totalCount", List.of(
                        new Document("$count", "count")
                ))
        );
        operations.add(context -> facetStage);

        Aggregation aggregation = Aggregation.newAggregation(operations);

        Document result = mongoTemplate.aggregate(aggregation, "invoices", Document.class)
                .getUniqueMappedResult();

        List<Invoice> invoices = new ArrayList<>();
        long total = 0;

        if (result != null) {
            List<Document> resultDocs = result.getList("results", Document.class);
            for (Document doc : resultDocs) {
                invoices.add(mongoTemplate.getConverter().read(Invoice.class, doc));
            }

            List<Document> countDoc = result.getList("totalCount", Document.class);
            if (!countDoc.isEmpty()) {
                total = countDoc.get(0).getInteger("count");
            }
        }

        return new PageImpl<>(invoices, pageable, total);
    }

    public Optional<Invoice> getInvoiceById(ObjectId id){
        return invoiceRepository.findBy_idEqualsAndAudit_DeletedAtIsNull(id);
    }

    public Invoice insertInvoice(Invoice invoice) {
        Invoice saved = invoiceRepository.insert(invoice);
        invoiceSearchService.syncInvoice(saved);
        return saved;
    }

    public Invoice updateInvoice(Invoice invoice) {
        Invoice saved = invoiceRepository.save(invoice);
        invoiceSearchService.syncInvoice(saved);
        return saved;
    }
}