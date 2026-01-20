package com.dashboard.controller.invoices;

import com.dashboard.common.logging.GrafanaHttpClient;
import com.dashboard.common.model.Audit;
import com.dashboard.controller.InvoicesController;
import com.dashboard.mapper.interfaces.ICustomerMapper;
import com.dashboard.mapper.interfaces.IInvoiceMapper;
import com.dashboard.mapper.interfaces.IInvoiceSearchMapper;
import com.dashboard.model.entities.Customer;
import com.dashboard.model.entities.Invoice;
import com.dashboard.service.interfaces.ICustomerService;
import com.dashboard.service.interfaces.IInvoiceSearchService;
import com.dashboard.service.interfaces.IInvoiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.time.LocalDate;

@WebMvcTest(InvoicesController.class)
@AutoConfigureMockMvc(addFilters = false)
public abstract class BaseInvoicesControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected IInvoiceService invoiceService;

    @MockitoBean
    protected IInvoiceSearchService invoiceSearchService;

    @MockitoBean
    protected ICustomerService customersService;

    @MockitoBean
    protected IInvoiceMapper invoiceMapper;

    @MockitoBean
    protected ICustomerMapper customerMapper;

    @MockitoBean
    protected IInvoiceSearchMapper invoiceSearchMapper;

    @MockitoBean
    protected GrafanaHttpClient grafanaHttpClient;

    protected final Faker faker = new Faker();

    protected ObjectId testInvoiceId;
    protected ObjectId testCustomerId;

    protected Double testAmount;
    protected LocalDate testDate;
    protected String testStatus;

    @BeforeEach
    void setUpBase() {
        testInvoiceId = new ObjectId();
        testCustomerId = new ObjectId();
        testAmount = faker.number().randomDouble(2, 100, 10000);
        testDate = LocalDate.now();
        testStatus = faker.lorem().word();
    }

    protected Invoice createTestInvoice() {
        Audit audit = new Audit();
        audit.setCreatedAt(Instant.now());

        Customer customer = new Customer();
        customer.set_id(testCustomerId);
        customer.setName(faker.name().fullName());
        customer.setEmail(faker.internet().emailAddress());
        customer.setImage_url(faker.internet().url());
        customer.setAudit(audit);

        Invoice invoice = new Invoice();
        invoice.set_id(testInvoiceId);
        invoice.setAmount(testAmount);
        invoice.setDate(testDate);
        invoice.setStatus(testStatus);
        invoice.setCustomer(customer);
        invoice.setAudit(audit);

        return invoice;
    }
}
