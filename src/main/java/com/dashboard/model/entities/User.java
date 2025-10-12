package com.dashboard.model.entities;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {
    @Id
    private ObjectId _id;
    private String name;
    private String email;
    private String password;
    private Audit audit;
}
