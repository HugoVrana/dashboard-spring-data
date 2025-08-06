package com.dashboard.repository;

import com.dashboard.model.Invoice;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface IInvoiceRepository extends MongoRepository<Invoice, ObjectId> {
    List<Invoice> findByStatus(String status);

    @Query("{ '$or': [ " +
            "  { 'status': { $regex: ?0, $options: 'i' } }, " +
            "  { 'date': { $regex: ?0, $options: 'i' } }, " +
            "  { 'description': { $regex: ?0, $options: 'i' } }, " +
            "  { $expr: { $regexMatch: { input: { $toString: '$amount' }, regex: ?0, options: 'i' } } } " +
            "] }")
    List<Invoice> searchInvoices(String searchTerm);
}
