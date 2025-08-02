package com.dashboard.repository;

import com.dashboard.model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.UUID;

public interface IInvoiceRepository extends MongoRepository<Invoice, UUID> {
}
