package com.dashboard.controller;

import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.logging.GrafanaHttpClient;
import com.dashboard.mapper.CustomerMapper;
import com.dashboard.model.Customer;
import com.dashboard.model.exception.ResourceNotFoundException;
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
    private final GrafanaHttpClient grafanaHttpClient;

    public CustomersController(ICustomerService customersService, CustomerMapper customerMapper, GrafanaHttpClient grafanaHttpClient) {
        this.customersService = customersService;
        this.customerMapper = customerMapper;
        this.grafanaHttpClient = grafanaHttpClient;
    }

    @GetMapping("/")
    public ResponseEntity<List<CustomerRead>> getAllCustomers() {
        List<Customer> customers = customersService.getAllCustomers();
        List<CustomerRead> customerDtos = new ArrayList<>();
        for(Customer customer : customers) {
            CustomerRead customerDto = customerMapper.toRead(customer);
            customerDtos.add(customerDto);
        }

        grafanaHttpClient.send();
        return ResponseEntity.ok(customerDtos);
    }

    // does not work
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
