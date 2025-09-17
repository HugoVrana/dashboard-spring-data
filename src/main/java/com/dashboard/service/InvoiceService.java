package com.dashboard.service;

import com.dashboard.model.Invoice;
import com.dashboard.repository.IInvoiceRepository;
import com.dashboard.service.interfaces.IInvoiceService;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Scope("singleton")
public class InvoiceService implements IInvoiceService {
    private final IInvoiceRepository invoiceRepository;
    private final MongoTemplate mongoTemplate;

    public InvoiceService(IInvoiceRepository invoiceRepository, MongoTemplate mongoTemplate) {
        this.invoiceRepository = invoiceRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public  List<Invoice> getInvoicesByStatus(String status){
        return invoiceRepository.findByStatus(status);
    }

    public List<Invoice> getLatestInvoice(Integer indexFrom, Integer indexTo) {
        return invoiceRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Invoice::getDate).reversed()) // latest first
                .skip(indexFrom)
                .limit(indexTo - indexFrom + 1)
                .toList();
    }

    public Page<Invoice> searchInvoices(String rawTerm, Pageable pageable) {
        if (rawTerm == null || rawTerm.trim().isEmpty()) {
            List<Invoice> allInvoices = invoiceRepository.findAll();
            long count = allInvoices.size();
            return new PageImpl<>(allInvoices, pageable, count);
        }

        String term = rawTerm.trim();
        List<Criteria> ors = new ArrayList<>();

        // _id
        if (ObjectId.isValid(term)) {
            ors.add(Criteria.where("_id").is(new ObjectId(term)));
            ors.add(Criteria.where("customer._id").is(new ObjectId(term))); // can't add another field for now
        }

        // amount (exact numeric)
        parseBigDecimal(term).ifPresent(bd -> ors.add(Criteria.where("amount").is(bd)));

        // date exact yyyy-MM-dd
        parseIsoLocalDate(term).ifPresent(d -> ors.add(Criteria.where("date").is(d)));

        // year-month (yyyy-MM) => range on date
        parseYearMonth(term).ifPresent(ym -> {
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();
            ors.add(Criteria.where("date").gte(start).lte(end));
        });

        // year (yyyy) => range on date
        parseYear(term).ifPresent(y -> {
            LocalDate start = LocalDate.of(y, 1, 1);
            LocalDate end = LocalDate.of(y, 12, 31);
            ors.add(Criteria.where("date").gte(start).lte(end));
        });

        // case-insensitive substring on selected string fields
        Pattern containsCi = Pattern.compile(Pattern.quote(term), Pattern.CASE_INSENSITIVE);
        ors.add(Criteria.where("status").regex(containsCi));
        // customer id already filtered above
        Query q = new Query(new Criteria().orOperator(ors.toArray(new Criteria[0]))).with(pageable);

        List<Invoice> results = mongoTemplate.find(q, Invoice.class);
        long total = mongoTemplate.count(new Query(new Criteria().orOperator(ors.toArray(new Criteria[0]))), Invoice.class);

        return new PageImpl<>(results, pageable, total);
    }

    public Invoice insertInvoice(Invoice invoice) {
        return invoiceRepository.insert(invoice);
    }

    private Optional<BigDecimal> parseBigDecimal(String s) {
        try { return Optional.of(new BigDecimal(s)); } catch (Exception e) { return Optional.empty(); }
    }

    private Optional<LocalDate> parseIsoLocalDate(String s) {
        try { return Optional.of(LocalDate.parse(s)); } catch (Exception e) { return Optional.empty(); }
    }

    private Optional<YearMonth> parseYearMonth(String s) {
        try { return Optional.of(YearMonth.parse(s)); } catch (Exception e) { return Optional.empty(); }
    }

    private Optional<Integer> parseYear(String s) {
        try {
            if (s.length() == 4) return Optional.of(Integer.parseInt(s));
            return Optional.empty();
        } catch (Exception e) { return Optional.empty(); }
    }
}