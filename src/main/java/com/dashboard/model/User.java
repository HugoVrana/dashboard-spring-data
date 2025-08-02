package com.dashboard.model;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class User {
    public ObjectId id;
    public String name;
    public String email;
    public String password;
}
