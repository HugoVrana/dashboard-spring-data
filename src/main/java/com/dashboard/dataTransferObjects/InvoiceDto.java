package com.dashboard.dataTransferObjects;

import lombok.Data;
import java.time.LocalDate;

@Data
public class InvoiceDto {
    public String id;
    public String status;
    public Double amount;
    public LocalDate date;
    public CustomerDto customer;
}