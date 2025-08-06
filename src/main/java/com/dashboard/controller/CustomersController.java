package com.dashboard.controller;

import com.dashboard.dataTransferObjects.CustomerDto;
import com.dashboard.mappers.CustomerMapper;
import com.dashboard.model.Customer;
import com.dashboard.service.CustomersService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

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
    public List<CustomerDto> getAllCustomers() {
        List<Customer> customers = customersService.getAllCustomers();
        List<CustomerDto> customerDtos = new ArrayList<>();
        for(Customer customer : customers) {
            CustomerDto customerDto = customerMapper.toDto(customer);
            customerDtos.add(customerDto);
        }
        return customerDtos;
    }

    @GetMapping("/count")
    public Integer getCustomerCount() {
        return customersService.getAllCustomers().size();
    }
}
