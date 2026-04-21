package com.dashboard.model.entities;

import com.dashboard.common.model.Audit;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "oauth_clients")
public class OAuthClientDocument {
    @Id
    private ObjectId _id;
    private List<String> allowedHosts;
    private Audit audit;
}
