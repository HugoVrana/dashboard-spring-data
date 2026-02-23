package com.dashboard.model.entities;

import com.dashboard.common.model.Audit;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Month;
import java.time.Year;

@Data
@Document(collection = "revenues")
@CompoundIndex(name = "month_year_unique", def = "{'month': 1, 'year': 1}", unique = true)
public class Revenue {
    @Id
    private ObjectId _id;
    private Month month;
    private Year year;
    private Double revenue;
    private Audit audit;
}