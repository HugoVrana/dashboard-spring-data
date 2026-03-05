package com.dashboard.service;

import com.dashboard.authentication.GrantsAuthentication;
import com.dashboard.common.model.ActivityEvent;
import com.dashboard.common.model.Audit;
import com.dashboard.common.model.exception.ConflictException;
import com.dashboard.common.model.exception.ResourceNotFoundException;
import com.dashboard.dataTransferObject.customer.CustomerCreate;
import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.dataTransferObject.customer.CustomerUpdate;
import com.dashboard.environment.R2Properties;
import com.dashboard.mapper.interfaces.ICustomerMapper;
import com.dashboard.model.ActivityEventType;
import com.dashboard.model.entities.Customer;
import com.dashboard.repository.ICustomerRepository;
import com.dashboard.service.interfaces.IActivityFeedService;
import com.dashboard.service.interfaces.ICustomerService;
import com.dashboard.service.interfaces.IR2Service;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Scope("singleton")
@RequiredArgsConstructor
public class CustomerService implements ICustomerService {
    private final ICustomerRepository customerRepository;
    private final IActivityFeedService activityFeedService;
    private final ICustomerMapper customerMapper;
    private final IR2Service r2Service;

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
                "Email", customer.getEmail()
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
                "Image", saved.getImageId() != null ? saved.getImageId() : ""
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
                "Image", customer.getImageId() != null ? customer.getImageId() : ""
        ));
    }

    @Override
    public String setCustomerImage(String id, MultipartFile file) {
        Customer customer = getCustomerOrThrow(id);

        // Delete old image if exists
        if (customer.getImageId() != null) {
            String oldR2Key = R2Properties.buildR2Key(customer.get_id(), customer.getImageId());
            r2Service.deleteFile(oldR2Key);
        }

        // Upload new image
        String[] result = r2Service.uploadFile(file, customer.get_id());
        if (result.length < 3) {
            throw new ResourceNotFoundException("Image upload failed");
        }

        String publicUrl = result[0];
        String imageObjectId = result[2];

        if (imageObjectId == null || imageObjectId.isEmpty() || !ObjectId.isValid(imageObjectId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image ID returned");
        }

        // Update customer with new image ID
        ObjectId newImageId = new ObjectId(imageObjectId);
        customer.setImageId(newImageId);
        CustomerUpdate customerUpdate = customerMapper.toUpdate(customer);
        updateCustomer(customer.get_id().toHexString(), customerUpdate);

        return publicUrl;
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
        return customerRepository.insert(customer);
    }

    private Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
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
