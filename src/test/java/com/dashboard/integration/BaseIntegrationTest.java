package com.dashboard.integration;

import com.dashboard.common.model.Audit;
import com.dashboard.config.TestJwtTokenGenerator;
import com.dashboard.model.entities.Customer;
import com.dashboard.model.entities.Invoice;
import com.dashboard.model.entities.Revenue;
import com.dashboard.model.entities.User;
import com.dashboard.repository.ICustomersRepository;
import com.dashboard.repository.IInvoiceRepository;
import com.dashboard.repository.IInvoiceSearchRepository;
import com.dashboard.repository.IRevenueRepository;
import com.dashboard.repository.IUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Epic;
import net.datafaker.Faker;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

/**
 * Base class for full-stack integration tests using Testcontainers MongoDB.
 */
@Epic("Integration Tests")
@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseIntegrationTest {

    @ServiceConnection
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
            .waitingFor(Wait.forLogMessage("(?i).*waiting for connections.*\\n", 1))
            .withStartupTimeout(Duration.ofMinutes(2));

    static {
        mongoDBContainer.start();
    }

    protected static final Faker faker = new Faker();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ICustomersRepository customersRepository;

    @Autowired
    protected IInvoiceRepository invoiceRepository;

    @Autowired
    protected IInvoiceSearchRepository invoiceSearchRepository;

    @Autowired
    protected IUserRepository userRepository;

    @Autowired
    protected IRevenueRepository revenueRepository;

    @BeforeEach
    void cleanDatabase() {
        invoiceSearchRepository.deleteAll();
        invoiceRepository.deleteAll();
        customersRepository.deleteAll();
        userRepository.deleteAll();
        revenueRepository.deleteAll();
    }

    /**
     * Creates and saves an active customer to the database.
     */
    protected Customer createAndSaveCustomer() {
        Customer customer = new Customer();
        customer.set_id(new ObjectId());
        customer.setName(faker.name().fullName());
        customer.setEmail(faker.internet().emailAddress());
        customer.setImageUrl(faker.internet().url());
        customer.setAudit(createActiveAudit());
        return customersRepository.save(customer);
    }

    /**
     * Creates and saves an active invoice to the database.
     */
    protected Invoice createAndSaveInvoice(Customer customer) {
        Invoice invoice = new Invoice();
        invoice.set_id(new ObjectId());
        invoice.setCustomer(customer);
        invoice.setAmount(BigDecimal.valueOf(faker.number().randomDouble(2, 100, 10000))
                .setScale(2, RoundingMode.HALF_UP));
        invoice.setDate(LocalDate.now());
        invoice.setStatus(faker.options().option("pending", "paid", "cancelled"));
        invoice.setAudit(createActiveAudit());
        return invoiceRepository.save(invoice);
    }

    /**
     * Creates and saves an active invoice with a specific status.
     */
    protected Invoice createAndSaveInvoice(Customer customer, String status) {
        Invoice invoice = new Invoice();
        invoice.set_id(new ObjectId());
        invoice.setCustomer(customer);
        invoice.setAmount(BigDecimal.valueOf(faker.number().randomDouble(2, 100, 10000))
                .setScale(2, RoundingMode.HALF_UP));
        invoice.setDate(LocalDate.now());
        invoice.setStatus(status);
        invoice.setAudit(createActiveAudit());
        return invoiceRepository.save(invoice);
    }

    /**
     * Creates and saves an active user to the database.
     */
    protected User createAndSaveUser() {
        User user = new User();
        user.set_id(new ObjectId());
        user.setName(faker.name().fullName());
        user.setEmail(faker.internet().emailAddress());
        user.setPassword(faker.internet().password());
        user.setAudit(createActiveAudit());
        return userRepository.save(user);
    }

    /**
     * Creates and saves an active revenue to the database.
     */
    protected Revenue createAndSaveRevenue() {
        Revenue revenue = new Revenue();
        revenue.set_id(new ObjectId());
        revenue.setMonth(Month.of(faker.number().numberBetween(1, 12)));
        revenue.setYear(2024);
        revenue.setRevenue(BigDecimal.valueOf(faker.number().randomDouble(2, 1000, 100000))
                .setScale(2, RoundingMode.HALF_UP));
        revenue.setAudit(createActiveAudit());
        return revenueRepository.save(revenue);
    }

    /**
     * Creates an active (non-deleted) audit.
     */
    protected Audit createActiveAudit() {
        Audit audit = new Audit();
        audit.setCreatedAt(Instant.now());
        audit.setUpdatedAt(Instant.now());
        audit.setDeletedAt(null);
        return audit;
    }

    /**
     * Creates a soft-deleted audit.
     */
    protected Audit createDeletedAudit() {
        Audit audit = new Audit();
        audit.setCreatedAt(Instant.now().minusSeconds(3600));
        audit.setUpdatedAt(Instant.now().minusSeconds(1800));
        audit.setDeletedAt(Instant.now());
        return audit;
    }

    /**
     * Generates an Authorization header value with the specified grants.
     */
    protected String authHeader(String... grants) {
        String token = TestJwtTokenGenerator.generateValidToken("testUser", List.of(grants));
        return "Bearer " + token;
    }
}
