package com.dashboard.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "revenues")
public class Revenue {
    private ObjectId _id;
    private String month;
    private Double revenue;
    private Audit audit;
}
