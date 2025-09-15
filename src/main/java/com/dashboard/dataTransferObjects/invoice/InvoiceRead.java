package com.dashboard.dataTransferObjects.invoice;

import com.dashboard.dataTransferObjects.CustomerDto;
import lombok.Data;
import java.time.LocalDate;

@Data
public class InvoiceRead {
    public String id;
    public String status;
    public Double amount;
    public LocalDate date;
    public CustomerDto customer;
}