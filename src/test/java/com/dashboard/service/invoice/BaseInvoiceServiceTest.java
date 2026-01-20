package com.dashboard.service.invoice;

import com.dashboard.common.model.Audit;
import com.dashboard.model.entities.Customer;
import com.dashboard.model.entities.Invoice;
import com.dashboard.repository.IInvoiceRepository;
import com.dashboard.service.interfaces.IInvoiceSearchService;
import com.dashboard.service.interfaces.IInvoiceService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
public abstract class BaseInvoiceServiceTest {

    @MockitoBean
    protected IInvoiceRepository invoiceRepository;

    @MockitoBean
    protected IInvoiceSearchService invoiceSearchService;

    @Mock
    protected MongoTemplate mongoTemplate;

    @InjectMocks
    protected IInvoiceService invoiceService;

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
