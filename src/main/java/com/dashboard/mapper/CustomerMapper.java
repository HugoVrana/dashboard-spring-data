package com.dashboard.mapper;

import com.dashboard.dataTransferObject.customer.CustomerCreate;
import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.dataTransferObject.customer.CustomerUpdate;
import com.dashboard.environment.R2Properties;
import com.dashboard.mapper.interfaces.ICustomerMapper;
import com.dashboard.model.entities.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerMapper implements ICustomerMapper {

    private final R2Properties r2Properties;

    @Override
    public CustomerRead toRead(Customer customer) {
        CustomerRead customerDto = new CustomerRead();
        customerDto.setId(customer.get_id().toHexString());
        customerDto.setName(customer.getName());
        customerDto.setEmail(customer.getEmail());

        if (customer.getImageId() != null) {
            String url = r2Properties.buildPublicCustomerImageUrl(customer.get_id(), customer.getImageId());
            customerDto.setImageUrl(url);
        }
        return customerDto;
    }

    @Override
    public Customer toModel(CustomerCreate customerCreate) {
        Customer customer = new Customer();
        customer.setName(customerCreate.getName());
        customer.setEmail(customerCreate.getEmail());
        return customer;
    }

    @Override
    public CustomerUpdate toUpdate(Customer customer) {
        CustomerUpdate customerUpdate = new CustomerUpdate();
        customerUpdate.setId(customer.get_id().toHexString());
        customerUpdate.setName(customer.getName());
        customerUpdate.setEmail(customer.getEmail());
        customerUpdate.setImageId(customer.getImageId());
        return customerUpdate;
    }
}
