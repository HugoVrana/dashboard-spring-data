package com.dashboard.mappers;

import com.dashboard.dataTransferObjects.CustomerDto;
import com.dashboard.model.Customer;

public interface ICustomerMapper {

    CustomerDto toDto(Customer customer);
}
