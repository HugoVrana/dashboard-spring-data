package com.dashboard.dataTransferObjects;

import lombok.Data;
import java.util.Date;

@Data
public class InvoiceDto {
    public String id;
    public String status;
    public Double amount;
    public Date date;
    public CustomerDto customer;
}