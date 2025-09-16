package com.dashboard.controller;

import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.mapper.CustomerMapper;
import com.dashboard.model.Customer;
import com.dashboard.service.CustomersService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/customers")
public class CustomersController {
    private final CustomersService customersService;
    private final CustomerMapper customerMapper;

    public CustomersController(CustomersService customersService, CustomerMapper customerMapper) {
        this.customersService = customersService;
        this.customerMapper = customerMapper;
    }

    @GetMapping("/")
    public List<CustomerRead> getAllCustomers() {
        List<Customer> customers = customersService.getAllCustomers();
        List<CustomerRead> customerDtos = new ArrayList<>();
        for(Customer customer : customers) {
            CustomerRead customerDto = customerMapper.toRead(customer);
            customerDtos.add(customerDto);
        }
        return customerDtos;
    }

    @GetMapping("/{id}")
    public CustomerRead getCustomerById(String id) {
       Optional<Customer> c = customersService.getCustomer(new org.bson.types.ObjectId(id));
        return c.map(customerMapper::toRead).orElse(null);
    }

    @GetMapping("/count")
    public Integer getCustomerCount() {
        return customersService.getAllCustomers().size();
    }
}
