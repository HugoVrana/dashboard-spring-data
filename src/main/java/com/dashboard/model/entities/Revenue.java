package com.dashboard.model.entities;

import com.dashboard.common.model.Audit;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "revenues")
public class Revenue {
    @Id
    private ObjectId _id;
    private String month;
    private Double revenue;
    private Audit audit;
}
