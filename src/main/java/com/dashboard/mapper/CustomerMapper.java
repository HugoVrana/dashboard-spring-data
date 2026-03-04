package com.dashboard.mapper;

import com.dashboard.dataTransferObject.customer.CustomerCreate;
import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.mapper.interfaces.ICustomerMapper;
import com.dashboard.model.entities.Customer;
import org.springframework.stereotype.Service;

@Service
public class CustomerMapper implements ICustomerMapper {
    @Override
    public CustomerRead toRead(Customer customer) {
        CustomerRead customerDto = new CustomerRead();
        customerDto.setId(customer.get_id().toHexString());
        customerDto.setName(customer.getName());
        customerDto.setEmail(customer.getEmail());
        return customerDto;
    }

    @Override
    public Customer toModel(CustomerCreate customerCreate) {
        Customer customer = new Customer();
        customer.setName(customerCreate.getName());
        customer.setEmail(customerCreate.getEmail());
        return customer;
    }
}
