package com.dashboard.service;

import com.dashboard.model.Customer;
import com.dashboard.repository.ICustomersRepository;
import com.dashboard.service.interfaces.ICustomerService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Scope("singleton")
public class CustomersService implements ICustomerService {
    private final ICustomersRepository customersRepository;

    public CustomersService(ICustomersRepository customersRepository) {
        this.customersRepository = customersRepository;
    }

    public List<Customer> getAllCustomers() {
        return customersRepository.findAll();
    }
}
