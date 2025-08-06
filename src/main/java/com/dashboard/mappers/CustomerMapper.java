package com.dashboard.mappers;

import com.dashboard.dataTransferObjects.CustomerDto;
import com.dashboard.model.Customer;
import org.springframework.stereotype.Service;

@Service
public class CustomerMapper implements ICustomerMapper{
    @Override
    public CustomerDto toDto(Customer customer) {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(customer._id.toHexString());
        customerDto.setName(customer.getName());
        customerDto.setEmail(customer.getEmail());
        customerDto.setImage_url(customer.getImage_url());
        return customerDto;
    }
}
