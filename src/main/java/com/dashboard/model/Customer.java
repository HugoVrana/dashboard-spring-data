package com.dashboard.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "customers")
public class Customer {
    public ObjectId _id;
    public String name;
    public String email;
    public String image_url;
}