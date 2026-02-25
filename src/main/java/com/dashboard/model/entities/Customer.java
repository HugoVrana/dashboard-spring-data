package com.dashboard.model.entities;

import com.dashboard.common.model.Audit;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "customers")
public class Customer {
    @Id
    private ObjectId _id;
    private String name;
    private String email;
    @Field("image_url")
    private String imageUrl;
    private Audit audit;
}