package com.dashboard.model.entities;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Document(collection = "invoice_search")
@CompoundIndexes({
    @CompoundIndex(name = "customer_idx", def = "{'customerId': 1}"),
    @CompoundIndex(name = "deletedAt_idx", def = "{'deletedAt': 1}")
})
public class InvoiceSearchDocument {
    @Id
    private ObjectId _id;

    @Indexed
    private ObjectId invoiceId;

    @Indexed
    private ObjectId customerId;

    // Invoice fields
    private Double amount;
    private LocalDate date;

    @TextIndexed(weight = 2)
    private String status;

    // Flattened customer fields
    @TextIndexed(weight = 3)
    private String customerName;

    @TextIndexed(weight = 2)
    private String customerEmail;

    private String customerImageUrl;

    // Tracking
    private Instant deletedAt;
    private Instant lastSyncedAt;
}
