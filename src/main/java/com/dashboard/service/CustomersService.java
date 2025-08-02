package com.dashboard.service;

import com.dashboard.repository.ICustomersRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class CustomersService {
    private final ICustomersRepository customersRepository;

    public CustomersService(ICustomersRepository customersRepository) {
        this.customersRepository = customersRepository;
    }
}
