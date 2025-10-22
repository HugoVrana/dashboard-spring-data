package com.dashboard.repository;

import com.dashboard.model.entities.Customer;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface ICustomersRepository extends MongoRepository<Customer, ObjectId> {
    List<Customer> findByAudit_DeletedAtIsNull();

    Integer countByAudit_DeletedAtIsNull();

    Optional<Customer> findBy_idEqualsAndAudit_DeletedAtIsNull(ObjectId id);
}