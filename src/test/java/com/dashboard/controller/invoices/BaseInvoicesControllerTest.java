package com.dashboard.controller.invoices;

import com.dashboard.common.logging.GrafanaHttpClient;
import com.dashboard.common.model.Audit;
import com.dashboard.controller.InvoicesController;
import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.mapper.interfaces.ICustomerMapper;
import com.dashboard.mapper.interfaces.IInvoiceMapper;
import com.dashboard.mapper.interfaces.IInvoiceSearchMapper;
import com.dashboard.model.entities.Customer;
import com.dashboard.model.entities.Invoice;
import com.dashboard.model.entities.InvoiceSearchDocument;
import com.dashboard.service.interfaces.IActivityFeedService;
import com.dashboard.service.interfaces.ICustomerService;
import com.dashboard.service.interfaces.IInvoiceSearchService;
import com.dashboard.service.interfaces.IInvoiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import net.datafaker.Faker;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.dashboard.config.TestConfig;

import java.time.Instant;
import java.time.LocalDate;

@Epic("Invoices")
@Feature("Invoice API")
@Tag("controller-invoice")
@WebMvcTest(InvoicesController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestConfig.class)
@Execution(ExecutionMode.SAME_THREAD)
@ResourceLock("spring-context")
@WithMockUser(username = "testUser")
public abstract class BaseInvoicesControllerTest {

    protected final Faker faker = new Faker();
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
    @MockitoBean
    protected IActivityFeedService activityFeedService;
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
        customer.setImageUrl(faker.internet().url());
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

    protected InvoiceRead createTestInvoiceRead(Invoice invoice) {
        InvoiceRead invoiceRead = new InvoiceRead();
        invoiceRead.setId(invoice.get_id().toHexString());
        invoiceRead.setAmount(invoice.getAmount());
        invoiceRead.setDate(invoice.getDate());
        invoiceRead.setStatus(invoice.getStatus());

        CustomerRead customerRead = new CustomerRead();
        customerRead.setId(invoice.getCustomer().get_id().toHexString());
        customerRead.setName(invoice.getCustomer().getName());
        customerRead.setEmail(invoice.getCustomer().getEmail());
        customerRead.setImageUrl(invoice.getCustomer().getImageUrl());
        invoiceRead.setCustomer(customerRead);

        return invoiceRead;
    }

    protected InvoiceSearchDocument createTestInvoiceSearchDocument(Invoice invoice) {
        InvoiceSearchDocument doc = new InvoiceSearchDocument();
        doc.set_id(new ObjectId());
        doc.setInvoiceId(invoice.get_id());
        doc.setCustomerId(invoice.getCustomer().get_id());
        doc.setAmount(invoice.getAmount());
        doc.setDate(invoice.getDate());
        doc.setStatus(invoice.getStatus());
        doc.setCustomerName(invoice.getCustomer().getName());
        doc.setCustomerEmail(invoice.getCustomer().getEmail());
        doc.setCustomerImageUrl(invoice.getCustomer().getImageUrl());
        return doc;
    }
}
