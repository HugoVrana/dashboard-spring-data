package com.dashboard.repository;

import com.dashboard.model.entities.InvoiceSearchDocument;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface IInvoiceSearchRepository extends MongoRepository<InvoiceSearchDocument, ObjectId> {

    Optional<InvoiceSearchDocument> findByInvoiceIdAndDeletedAtIsNull(ObjectId invoiceId);

    List<InvoiceSearchDocument> findByCustomerId(ObjectId customerId);

    Page<InvoiceSearchDocument> findByDeletedAtIsNull(Pageable pageable);

    void deleteByInvoiceId(ObjectId invoiceId);
}
