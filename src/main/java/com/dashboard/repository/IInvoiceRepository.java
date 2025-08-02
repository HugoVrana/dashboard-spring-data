package com.dashboard.repository;

import com.dashboard.model.Invoice;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IInvoiceRepository extends MongoRepository<Invoice, ObjectId> {
}
