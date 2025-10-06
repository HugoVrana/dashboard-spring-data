package com.dashboard.service.interfaces;

import com.dashboard.model.Customer;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface ICustomerService {
    Optional<Customer> getCustomer(ObjectId id);
    List<Customer> getAllCustomers();
    Long getCount();
}
