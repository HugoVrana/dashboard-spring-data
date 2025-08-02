package com.dashboard.model;

import lombok.Data;
import org.bson.types.ObjectId;
import java.util.Date;

@Data
public class Invoice {
    public ObjectId _id;
    public ObjectId customerId;
    public Double amount;
    public Date date;
    public String status;
}
