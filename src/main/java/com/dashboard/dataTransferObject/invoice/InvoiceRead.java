package com.dashboard.dataTransferObject.invoice;

import com.dashboard.dataTransferObject.customer.CustomerRead;
import lombok.Data;
import java.time.LocalDate;

@Data
public class InvoiceRead {
    private String id;
    private String status;
    private Double amount;
    private LocalDate date;
    private CustomerRead customer;
}