package com.dashboard.service.interfaces;

import com.dashboard.dataTransferObject.customer.CustomerCreate;
import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.dataTransferObject.customer.CustomerUpdate;
import com.dashboard.model.entities.Customer;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

public interface ICustomerService {
    Optional<Customer> getCustomer(ObjectId id);
    List<Customer> getAllCustomers();
    Long getCount();
    CustomerRead createCustomer(CustomerCreate customerCreate);
    CustomerRead updateCustomer(String id, CustomerUpdate customerUpdate);
    void deleteCustomer(String id);
    String setCustomerImage(String id, MultipartFile file);
}