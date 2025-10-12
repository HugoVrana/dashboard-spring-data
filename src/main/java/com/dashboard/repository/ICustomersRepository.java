package com.dashboard.repository;

import com.dashboard.model.entities.Customer;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ICustomersRepository extends MongoRepository<Customer, ObjectId> {
    List<Customer> queryByAudit_DeletedAt(Instant auditDeletedAt);

    Optional<Customer> findBy_idEqualsAndAudit_DeletedAt(ObjectId id, Instant auditDeletedAt);
}