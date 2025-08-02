package com.dashboard.repository;

import com.dashboard.model.Customer;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ICustomersRepository extends MongoRepository<Customer, ObjectId> {
}