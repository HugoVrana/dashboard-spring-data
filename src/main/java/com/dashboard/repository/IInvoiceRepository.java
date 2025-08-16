package com.dashboard.repository;

import com.dashboard.model.Invoice;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface IInvoiceRepository extends MongoRepository<Invoice, ObjectId> {
    List<Invoice> findByStatus(String status);
}
