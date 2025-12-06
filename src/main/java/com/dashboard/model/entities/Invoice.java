package com.dashboard.model.entities;

import com.dashboard.common.model.Audit;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Data
@Document(collection = "invoices")
public class Invoice {
    @Id
    private ObjectId _id;
    @DBRef
    private Customer customer;
    private Double amount;
    private LocalDate date;
    private String status;
    private Audit audit;
}