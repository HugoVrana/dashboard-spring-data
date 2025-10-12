package com.dashboard.mapper;

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
        customerDto.setImage_url(customer.getImage_url());
        return customerDto;
    }
}
