package com.dashboard.repository;

import com.dashboard.model.Invoice;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface IInvoiceRepository extends MongoRepository<Invoice, ObjectId> {
    List<Invoice> queryByAudit_DeletedAt(Instant auditDeletedAt);

    List<Invoice> findByStatusAndAudit_DeletedAt(String status, Instant auditDeletedAt);

    Optional<Invoice> findBy_idEqualsAndAudit_DeletedAt(ObjectId id, Instant auditDeletedAt);
}
