package com.dashboard.service;

import com.dashboard.authentication.GrantsAuthentication;
import com.dashboard.common.model.ActivityEvent;
import com.dashboard.common.model.Audit;
import com.dashboard.common.model.exception.NotFoundException;
import com.dashboard.common.model.exception.ResourceNotFoundException;
import com.dashboard.dataTransferObject.invoice.InvoiceCreate;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.dataTransferObject.invoice.InvoiceUpdate;
import com.dashboard.mapper.interfaces.IInvoiceMapper;
import com.dashboard.model.ActivityEventType;
import com.dashboard.model.entities.Customer;
import com.dashboard.model.entities.Invoice;
import com.dashboard.repository.IInvoiceRepository;
import com.dashboard.service.interfaces.*;
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
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Scope("singleton")
@RequiredArgsConstructor
public class InvoiceService implements IInvoiceService {
    private final IInvoiceRepository invoiceRepository;
    private final MongoTemplate mongoTemplate;
    private final IInvoiceSearchService invoiceSearchService;
    private final ICustomerService customerService;
    private final IInvoiceMapper invoiceMapper;
    private final IActivityFeedService activityFeedService;
    private final IRevenueService revenueService;

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
                total = countDoc.getFirst().getInteger("count");
            }
        }

        return new PageImpl<>(invoices, pageable, total);
    }

    public Invoice getInvoiceById(String id) {
        return getInvoiceOrThrow(id);
    }

    public InvoiceRead createInvoice(InvoiceCreate invoiceCreate) {
        ObjectId customerId = new ObjectId(invoiceCreate.getCustomer_id());
        Customer customer = customerService.getCustomer(customerId)
                .orElseThrow(() -> new NotFoundException("The provided customer id does not exist"));

        Instant now = Instant.now();
        Audit audit = new Audit();
        audit.setCreatedAt(now);
        audit.setUpdatedAt(now);

        Invoice invoice = invoiceMapper.toModel(invoiceCreate, customer);
        invoice.setDate(LocalDate.now());
        invoice.setAudit(audit);
        invoice = insertInvoice(invoice);

        revenueService.adjustRevenue(invoice.getDate().getMonth(), invoice.getDate().getYear(), invoice.getAmount());
        publishActivityEvent(ActivityEventType.INVOICE_CREATED, invoice, Map.of(
                "amount", invoice.getAmount(),
                "status", invoice.getStatus()
        ));

        return invoiceMapper.toReadWithCustomer(invoice);
    }

    public InvoiceRead updateInvoice(String id, InvoiceUpdate invoiceUpdate) {
        Invoice existingInvoice = getInvoiceOrThrow(id);

        ObjectId customerId = new ObjectId(invoiceUpdate.getCustomerId());
        Customer customer = customerService.getCustomer(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id " + customerId + " not found"));

        Audit audit = existingInvoice.getAudit();
        audit.setUpdatedAt(Instant.now());

        Invoice invoice = invoiceMapper.toModel(invoiceUpdate, customer);
        invoice.setDate(existingInvoice.getDate());
        invoice.setAudit(audit);
        invoice = saveInvoice(invoice);

        publishActivityEvent(ActivityEventType.INVOICE_UPDATED, invoice, Map.of(
                "amount", invoice.getAmount(),
                "status", invoice.getStatus()
        ));

        return invoiceMapper.toReadWithCustomer(invoice);
    }

    public void deleteInvoice(String id) {
        Invoice invoice = getInvoiceOrThrow(id);

        Audit audit = invoice.getAudit();
        audit.setDeletedAt(Instant.now());
        invoice.setAudit(audit);
        saveInvoice(invoice);
        invoiceSearchService.markInvoiceDeleted(invoice.get_id());

        revenueService.adjustRevenue(invoice.getDate().getMonth(), invoice.getDate().getYear(), -invoice.getAmount());
        publishActivityEvent(ActivityEventType.INVOICE_DELETED, invoice, Map.of());
    }

    private Invoice insertInvoice(Invoice invoice) {
        Invoice saved = invoiceRepository.insert(invoice);
        invoiceSearchService.syncInvoice(saved);
        return saved;
    }

    private Invoice saveInvoice(Invoice invoice) {
        Invoice saved = invoiceRepository.save(invoice);
        invoiceSearchService.syncInvoice(saved);
        return saved;
    }

    private void publishActivityEvent(ActivityEventType type, Invoice invoice, Map<String, Object> extraMetadata) {
        GrantsAuthentication auth = GrantsAuthentication.current();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("invoiceId", invoice.get_id().toHexString());
        metadata.put("customerName", invoice.getCustomer().getName());
        metadata.put("userImageUrl", auth.getProfileImageUrlOrEmpty());
        metadata.putAll(extraMetadata);

        ActivityEvent event = ActivityEvent.builder()
                .id(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .type(type.name())
                .actorId(auth.getUserId())
                .metadata(metadata)
                .build();
        activityFeedService.publishEvent(event);
    }

    private Invoice getInvoiceOrThrow(String id) {
        if (!ObjectId.isValid(id)) {
            throw new ResourceNotFoundException("This id is invalid");
        }
        ObjectId objectId = new ObjectId(id);
        Optional<Invoice> optional = invoiceRepository.findBy_idEqualsAndAudit_DeletedAtIsNull(objectId);
        if (optional.isEmpty()) {
            throw new ResourceNotFoundException("This id is invalid");
        }
        return optional.get();
    }
}