package com.dashboard.service.interfaces;

import com.dashboard.model.Customer;
import org.bson.types.ObjectId;
import java.util.List;
import java.util.Optional;

public interface ICustomerService {
    List<Customer> getAllCustomers();
    Optional<Customer> getCustomer(ObjectId id);
}
