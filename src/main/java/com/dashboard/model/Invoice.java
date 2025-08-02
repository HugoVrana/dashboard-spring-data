package com.dashboard.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "invoices")
public class Invoice {
    public ObjectId _id;
    public ObjectId customer_id;
    public Double amount;
    public Date date;
    public String status;
}
