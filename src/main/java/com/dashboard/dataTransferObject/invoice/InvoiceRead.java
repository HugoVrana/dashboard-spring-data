package com.dashboard.dataTransferObject.invoice;

import com.dashboard.dataTransferObject.customer.CustomerRead;
import lombok.Data;
import java.time.LocalDate;

@Data
public class InvoiceRead {
    public String id;
    public String status;
    public Double amount;
    public LocalDate date;
    public CustomerRead customer;
}