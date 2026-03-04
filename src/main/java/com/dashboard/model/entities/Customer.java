package com.dashboard.model.entities;

import com.dashboard.common.model.Audit;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "customers")
public class Customer {
    @Id
    @JsonProperty("id")
    private ObjectId _id;
    private String name;
    @Indexed(unique = true)
    @Email
    private String email;
    private ObjectId imageId;
    private Audit audit;
}