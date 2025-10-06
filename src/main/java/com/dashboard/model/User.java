package com.dashboard.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {
    private ObjectId _id;
    private String name;
    private String email;
    private String password;
    private Audit audit;
}
