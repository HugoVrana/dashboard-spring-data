package com.dashboard.controller;

import com.dashboard.common.model.exception.ResourceNotFoundException;
import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.mapper.interfaces.ICustomerMapper;
import com.dashboard.model.entities.Customer;
import com.dashboard.service.interfaces.ICustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@Tag(name = "Customers", description = "Customer management operations")
@RequestMapping(value = "/customers", produces = "application/json")
@RequiredArgsConstructor
public class CustomersController {

    private final ICustomerService customersService;
    private final ICustomerMapper customerMapper;

    @Operation(summary = "Get all customers", description = "Retrieves a list of all customers")
    @GetMapping("/")
    @PreAuthorize("hasAuthority('dashboard-customers-read')")
    public ResponseEntity<List<CustomerRead>> getAllCustomers() {
        List<Customer> customers = customersService.getAllCustomers();
        List<CustomerRead> customerDtos = new ArrayList<>();
        for (Customer customer : customers) {
            CustomerRead customerDto = customerMapper.toRead(customer);
            customerDtos.add(customerDto);
        }
        return ResponseEntity.ok(customerDtos);
    }

    @Operation(summary = "Get customer by ID", description = "Retrieves a specific customer by their ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard-customers-read')")
    public ResponseEntity<CustomerRead> getCustomerById(@Parameter(description = "Customer ID") @PathVariable("id") String id) {
        if (!ObjectId.isValid(id)) {
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

    @Operation(summary = "Get customer count", description = "Returns the total number of customers")
    @GetMapping("/count")
    @PreAuthorize("hasAuthority('dashboard-customers-read')")
    public ResponseEntity<Long> getCustomerCount() {
        long count = customersService.getCount();
        return ResponseEntity.ok(count);
    }
}
