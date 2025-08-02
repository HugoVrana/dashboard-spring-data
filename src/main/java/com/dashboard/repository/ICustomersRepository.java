package com.dashboard.repository;

import com.dashboard.model.Customer;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ICustomersRepository extends MongoRepository<Customer, UUID> { }