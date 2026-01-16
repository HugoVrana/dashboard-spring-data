package com.dashboard.service.interfaces;

import com.dashboard.model.entities.Customer;
import com.dashboard.model.entities.Invoice;
import com.dashboard.model.entities.InvoiceSearchDocument;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IInvoiceSearchService {

    Page<InvoiceSearchDocument> search(String searchTerm, Pageable pageable);

    void syncInvoice(Invoice invoice);

    void syncCustomer(Customer customer);

    void markInvoiceDeleted(ObjectId invoiceId);

    void rebuildIndex();
}
