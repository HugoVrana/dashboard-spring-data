package com.dashboard.controller;

import com.dashboard.common.model.exception.ResourceNotFoundException;
import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.mapper.CustomerMapper;
import com.dashboard.model.entities.Customer;
import com.dashboard.service.interfaces.ICustomerService;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/customers")
public class CustomersController {

    private final ICustomerService customersService;
    private final CustomerMapper customerMapper;

    public CustomersController(ICustomerService customersService, CustomerMapper customerMapper) {
        this.customersService = customersService;
        this.customerMapper = customerMapper;
    }

    @GetMapping("/")
    public ResponseEntity<List<CustomerRead>> getAllCustomers() {
        List<Customer> customers = customersService.getAllCustomers();
        List<CustomerRead> customerDtos = new ArrayList<>();
        for(Customer customer : customers) {
            CustomerRead customerDto = customerMapper.toRead(customer);
            customerDtos.add(customerDto);
        }
        return ResponseEntity.ok(customerDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerRead> getCustomerById(@PathVariable("id") String id) {
        if(!ObjectId.isValid(id)) {
            throw new ResourceNotFoundException("This id is invalid");
        }

        ObjectId customerId = new ObjectId(id);
        Optional<Customer> optionalCustomer = customersService.getCustomer(customerId);
        if (optionalCustomer.isEmpty()) {
            throw new ResourceNotFoundException("Customer with id " + id + " not found");
        }

        Customer customer = optionalCustomer.get();
        CustomerRead customerDto = customerMapper.toRead(customer);
        return ResponseEntity.ok(customerDto);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCustomerCount() {
        long count = customersService.getCount();
        return ResponseEntity.ok(count);
    }
}
