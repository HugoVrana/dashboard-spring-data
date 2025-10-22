package com.dashboard.repository;

import com.dashboard.model.entities.Invoice;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface IInvoiceRepository extends MongoRepository<Invoice, ObjectId> {
    List<Invoice> findByAudit_DeletedAtIsNull();

    List<Invoice> findByStatusAndAudit_DeletedAtIsNull(String status);

    Optional<Invoice> findBy_idEqualsAndAudit_DeletedAtIsNull(ObjectId id);
}
