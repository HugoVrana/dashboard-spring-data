package com.dashboard.integration.security.authorization;

import com.dashboard.integration.BaseIntegrationTest;
import com.dashboard.model.entities.Customer;
import com.dashboard.model.entities.Invoice;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseAuthorizationSecurityTest extends BaseIntegrationTest {

    protected Customer testCustomer;
    protected Invoice testInvoice;

    @BeforeEach
    void setUpData() {
        testCustomer = createAndSaveCustomer();
        testInvoice = createAndSaveInvoice(testCustomer);
    }
}
