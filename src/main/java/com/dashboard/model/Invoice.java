package com.dashboard.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "invoices")
public class Invoice {
    @Id
    public ObjectId _id;
    @DBRef
    public Customer customer;
    public Double amount;
    public Date date;
    public String status;

}