package com.dashboard.service.customer;

import com.dashboard.common.model.Audit;
import com.dashboard.model.entities.Customer;
import com.dashboard.repository.ICustomersRepository;
import com.dashboard.service.CustomerService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Epic("Customers")
@Feature("Customer Service")
@Tag("service-customer")
@ExtendWith(MockitoExtension.class)
public abstract class BaseCustomerServiceTest {

    @Mock
    protected ICustomersRepository customersRepository;

    @InjectMocks
    protected CustomerService customerService;

    protected Customer testCustomer;
    protected ObjectId testCustomerId;

    @BeforeEach
    void setUp() {
        testCustomerId = new ObjectId();
        testCustomer = new Customer();
        testCustomer.set_id(testCustomerId);
        testCustomer.setName("Acme Corp");
        testCustomer.setEmail("contact@acme.com");
        testCustomer.setImage_url("https://example.com/image.png");
        testCustomer.setAudit(new Audit());
    }
}
