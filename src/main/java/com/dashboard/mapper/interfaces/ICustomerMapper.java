package com.dashboard.mapper.interfaces;

import com.dashboard.dataTransferObject.customer.CustomerCreate;
import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.dataTransferObject.customer.CustomerUpdate;
import com.dashboard.model.entities.Customer;

public interface ICustomerMapper {

    CustomerRead toRead(Customer customer);
    Customer toModel(CustomerCreate customerCreate);
    CustomerUpdate toUpdate(Customer customer);
}
