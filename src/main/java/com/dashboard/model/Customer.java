package com.dashboard.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "customers")
public class Customer {
    private ObjectId _id;
    private String name;
    private String email;
    private String image_url;
    private Audit audit;
}