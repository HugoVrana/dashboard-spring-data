package com.dashboard.service;

import com.dashboard.authentication.GrantsAuthentication;
import com.dashboard.common.model.ActivityEvent;
import com.dashboard.common.model.Audit;
import com.dashboard.common.model.exception.ConflictException;
import com.dashboard.dataTransferObject.customer.CustomerCreate;
import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.dataTransferObject.customer.CustomerUpdate;
import com.dashboard.model.ActivityEventType;
import com.dashboard.model.entities.Customer;
import com.dashboard.common.model.exception.ResourceNotFoundException;
import com.dashboard.mapper.interfaces.ICustomerMapper;
import com.dashboard.repository.ICustomerRepository;
import com.dashboard.service.interfaces.IActivityFeedService;
import com.dashboard.service.interfaces.ICustomerService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

@Service
@Scope("singleton")
@RequiredArgsConstructor
public class CustomerService implements ICustomerService {
    private final ICustomerRepository customerRepository;
    private final IActivityFeedService activityFeedService;
    private final ICustomerMapper customerMapper;

    public List<Customer> getAllCustomers() {
        return customerRepository.findByAudit_DeletedAtIsNull();
    }

    public Optional<Customer> getCustomer(ObjectId id) {
        return customerRepository.findBy_idEqualsAndAudit_DeletedAtIsNull(id);
    }

    public Long getCount() {
        return (long) customerRepository.countByAudit_DeletedAtIsNull();
    }

    @Override
    public CustomerRead createCustomer(CustomerCreate customerCreate) {
        Optional<Customer> optionalCustomer = customerRepository.getCustomerByEmail(customerCreate.getEmail());
        if (optionalCustomer.isPresent()) {
            throw new ConflictException("Customer already exists");
        }

        Instant now = Instant.now();
        Audit audit = new Audit();
        audit.setCreatedAt(now);
        audit.setUpdatedAt(now);

        Customer customer = customerMapper.toModel(customerCreate);
        customer.setAudit(audit);
        customer = insertCustomer(customer);

        publishActivityEvent(ActivityEventType.CUSTOMER_CREATED, customer, Map.of(
                "Name", customer.getName(),
                "Email", customer.getEmail(),
                "Image", customer.getImageId()
        ));
        return customerMapper.toRead(customer);
    }

    @Override
    public CustomerRead updateCustomer(String id, CustomerUpdate customerUpdate) {
        Customer existingCustomer = getCustomerOrThrow(id);

        Audit audit = existingCustomer.getAudit();
        audit.setUpdatedAt(Instant.now());

        existingCustomer.setName(customerUpdate.getName());
        existingCustomer.setEmail(customerUpdate.getEmail());
        existingCustomer.setImageId(customerUpdate.getImageId());
        existingCustomer.setAudit(audit);

        Customer saved = saveCustomer(existingCustomer);

        publishActivityEvent(ActivityEventType.CUSTOMER_UPDATED, saved, Map.of(
                "Name", saved.getName(),
                "Email", saved.getEmail(),
                "Image", saved.getImageId()
        ));

        return customerMapper.toRead(saved);
    }

    @Override
    public void deleteCustomer(String id) {
        Customer customer = getCustomerOrThrow(id);
        Audit audit = customer.getAudit();
        audit.setDeletedAt(Instant.now());
        customer.setAudit(audit);
        saveCustomer(customer);

        publishActivityEvent(ActivityEventType.CUSTOMER_DELETED, customer, Map.of(
                "Name", customer.getName(),
                "Email", customer.getEmail(),
                "Image", customer.getImageId()
        ));
    }

    private Customer getCustomerOrThrow(String id) {
        if (!ObjectId.isValid(id)) {
            throw new ResourceNotFoundException("Invalid customer id");
        }
        ObjectId objectId = new ObjectId(id);
        return customerRepository.findBy_idEqualsAndAudit_DeletedAtIsNull(objectId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id " + id + " not found"));
    }

    private Customer insertCustomer(Customer customer) {
        Customer saved = customerRepository.insert(customer);
        return saved;
    }

    private Customer saveCustomer(Customer customer) {
        Customer saved = customerRepository.save(customer);
        return saved;
    }

    private void publishActivityEvent(ActivityEventType type, Customer customer, Map<String, Object> extraMetadata) {
        GrantsAuthentication auth = GrantsAuthentication.current();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("customerId", customer.get_id().toHexString());
        metadata.put("customerName", customer.getName());
        metadata.put("userImageUrl", auth.getProfileImageUrlOrEmpty());
        metadata.putAll(extraMetadata);

        ActivityEvent event = ActivityEvent.builder()
                .id(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .type(type.name())
                .actorId(auth.getUserId())
                .metadata(metadata)
                .build();
        activityFeedService.publishEvent(event);

    }
}
