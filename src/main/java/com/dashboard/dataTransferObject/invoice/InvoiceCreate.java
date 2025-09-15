package com.dashboard.dataTransferObject.invoice;

import lombok.Data;

@Data
public class InvoiceCreate {
    public String status;
    public Double amount;
    public String customer_id;
}
