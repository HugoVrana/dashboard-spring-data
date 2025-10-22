package com.dashboard.service;

import com.dashboard.model.entities.Customer;
import com.dashboard.repository.ICustomersRepository;
import com.dashboard.service.interfaces.ICustomerService;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@Scope("singleton")
public class CustomerService implements ICustomerService {
    private final ICustomersRepository customersRepository;

    public CustomerService(ICustomersRepository customersRepository) {
        this.customersRepository = customersRepository;
    }

    public List<Customer> getAllCustomers() {
        return customersRepository.findByAudit_DeletedAtIsNull();
    }

    public Optional<Customer> getCustomer(ObjectId id){
        return customersRepository.findBy_idEqualsAndAudit_DeletedAtIsNull(id);
    }

    public Long getCount() {return (long) customersRepository.countByAudit_DeletedAtIsNull();}
}
