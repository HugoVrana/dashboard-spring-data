package com.dashboard.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "revenues")
public class Revenue {
    public ObjectId _id;
    public String month;
    public Double revenue;
}
