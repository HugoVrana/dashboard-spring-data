package com.dashboard.service.invoice;

import com.dashboard.common.model.Audit;
import com.dashboard.mapper.interfaces.IInvoiceMapper;
import com.dashboard.model.entities.Customer;
import com.dashboard.model.entities.Invoice;
import com.dashboard.repository.IInvoiceRepository;
import com.dashboard.service.InvoiceService;
import com.dashboard.service.interfaces.IActivityFeedService;
import com.dashboard.service.interfaces.ICustomerService;
import com.dashboard.service.interfaces.IInvoiceSearchService;
import com.dashboard.service.interfaces.IRevenueService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDate;

@Epic("Invoices")
@Feature("Invoice Service")
@Tag("service-invoice")
@ExtendWith(MockitoExtension.class)
public abstract class BaseInvoiceServiceTest {

    @Mock
    protected IInvoiceRepository invoiceRepository;

    @Mock
    protected MongoTemplate mongoTemplate;

    @Mock
    protected IInvoiceSearchService invoiceSearchService;

    @Mock
    protected ICustomerService customerService;

    @Mock
    protected IInvoiceMapper invoiceMapper;

    @Mock
    protected IActivityFeedService activityFeedService;

    @Mock
    protected IRevenueService revenueService;

    @InjectMocks
    protected InvoiceService invoiceService;

    protected Invoice testInvoice;
    protected ObjectId testInvoiceId;

    @BeforeEach
    void setUp() {
        ObjectId testCustomerId = new ObjectId();
        Customer testCustomer = new Customer();
        testCustomer.set_id(testCustomerId);
        testCustomer.setName("Acme Corp");
        testCustomer.setEmail("billing@acme.com");
        testCustomer.setAudit(new Audit());

        testInvoiceId = new ObjectId();
        testInvoice = new Invoice();
        testInvoice.set_id(testInvoiceId);
        testInvoice.setCustomer(testCustomer);
        testInvoice.setAmount(1500.00);
        testInvoice.setDate(LocalDate.now());
        testInvoice.setStatus("pending");
        testInvoice.setAudit(new Audit());
    }
}
