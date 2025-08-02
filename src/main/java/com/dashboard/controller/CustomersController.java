package com.dashboard.controller;

import com.dashboard.model.Customer;
import com.dashboard.service.interfaces.ICustomerService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/customers")
public class CustomersController {

    private final ICustomerService customersService;

    public CustomersController(ICustomerService customersService) {
        this.customersService = customersService;
    }

    @GetMapping("/")
    public List<Customer> getAllCustomers() {
        return customersService.getAllCustomers();
    }
}
