package com.dashboard.controller.v1;

import com.dashboard.common.model.exception.ResourceNotFoundException;
import com.dashboard.dataTransferObject.customer.CustomerCreate;
import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.dataTransferObject.customer.CustomerUpdate;
import com.dashboard.mapper.interfaces.ICustomerMapper;
import com.dashboard.model.entities.Customer;
import com.dashboard.service.interfaces.ICustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer management operations")
@RequestMapping(value = "api/v1/customers", produces = "application/json")
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

    @Operation(summary = "Create customer", description = "Creates a new customer")
    @PostMapping()
    @PreAuthorize("hasAuthority('dashboard-customers-create')")
    public ResponseEntity<CustomerRead> createCustomer(@Valid @RequestBody CustomerCreate customerCreate) {
        CustomerRead created = customersService.createCustomer(customerCreate);
        URI location = URI.create("/customers/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Upload image of the customer", description = "Uploads an image of a customer")
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('dashboard-customers-update')")
    public ResponseEntity<String> setImage(
            @Parameter(description = "Customer ID") @PathVariable("id") String id,
            @Parameter(description = "Image file", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(type = "string", format = "binary")))
            @RequestParam("file") MultipartFile file) {
        String publicUrl = customersService.setCustomerImage(id, file);
        return ResponseEntity.ok(publicUrl);
    }

    @Operation(summary = "Update customer", description = "Update existing customer")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard-customers-update')")
    public ResponseEntity<CustomerRead> updateCustomer(
            @Parameter(description = "Customer ID") @PathVariable("id") String id,
            @Valid @RequestBody CustomerUpdate customerUpdate) {
        CustomerRead updated = customersService.updateCustomer(id, customerUpdate);
        URI location = URI.create("/invoices/" + updated.getId());
        return ResponseEntity.created(location).body(updated);
    }

    @Operation(summary = "Delete customer", description = "Soft deletes a customer by its ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard-customers-delete')")
    public ResponseEntity<Integer> deleteCustomer(@Parameter(description = "Customer ID") @PathVariable("id") String id) {
        customersService.deleteCustomer(id);
        return ResponseEntity.ok(1);
    }
}
