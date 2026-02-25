package com.dashboard.controller.customers;

import com.dashboard.common.logging.GrafanaHttpClient;
import com.dashboard.common.model.Audit;
import com.dashboard.controller.CustomersController;
import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.mapper.interfaces.ICustomerMapper;
import com.dashboard.model.entities.Customer;
import com.dashboard.service.interfaces.ICustomerService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import net.datafaker.Faker;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.dashboard.config.TestConfig;

import java.time.Instant;

@Epic("Customers")
@Feature("Customer API")
@Tag("controller-customer")
@WebMvcTest(CustomersController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestConfig.class)
@Execution(ExecutionMode.SAME_THREAD)
@ResourceLock("spring-context")
@WithMockUser(username = "testUser")
public abstract class BaseCustomersControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected ICustomerService customersService;

    @MockitoBean
    protected ICustomerMapper customerMapper;

    @MockitoBean
    protected GrafanaHttpClient grafanaHttpClient;

    protected final Faker faker = new Faker();

    protected ObjectId testCustomerId;
    protected String testCustomerName;
    protected String testCustomerEmail;
    protected String testImageUrl;

    @BeforeEach
    void setUpBase() {
        testCustomerId = new ObjectId();
        testCustomerName = faker.name().fullName();
        testCustomerEmail = faker.internet().emailAddress();
        testImageUrl = faker.internet().url();
    }

    protected Customer createTestCustomer() {
        Customer testCustomer = new Customer();
        testCustomer.set_id(testCustomerId);
        testCustomer.setName(testCustomerName);
        testCustomer.setEmail(testCustomerEmail);
        testCustomer.setImageUrl(testImageUrl);

        Audit audit = new Audit();
        audit.setCreatedAt(Instant.now());
        testCustomer.setAudit(audit);

        return testCustomer;
    }

    protected CustomerRead createTestCustomerRead() {
        CustomerRead customerRead = new CustomerRead();
        customerRead.setId(testCustomerId.toHexString());
        customerRead.setName(testCustomerName);
        customerRead.setEmail(testCustomerEmail);
        customerRead.setImageUrl(testImageUrl);
        return customerRead;
    }
}