package com.dashboard.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {
    public ObjectId _id;
    public String name;
    public String email;
    public String password;
}
