package com.dashboard.model;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class Customer {
    public ObjectId _id;
    public String name;
    public String email;
    public String image_url;
}