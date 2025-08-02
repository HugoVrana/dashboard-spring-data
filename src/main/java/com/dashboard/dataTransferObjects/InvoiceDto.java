package com.dashboard.dataTransferObjects;

import com.dashboard.model.Invoice;
import lombok.Data;
import java.util.Date;

@Data
public class InvoiceDto {
    public String id;
    public String status;
    public Double amount;
    public Date date;
    public CustomerDto customer;

    public InvoiceDto(Invoice invoice) {
        this.id = invoice.get_id().toHexString();
        this.status = invoice.getStatus();
        this.amount = invoice.getAmount();
        this.date = invoice.getDate();
        if (invoice.getCustomer() != null) {
            this.customer = new CustomerDto(invoice.getCustomer());
        }
    }
}