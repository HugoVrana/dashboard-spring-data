package com.dashboard.repository;

import com.dashboard.model.Revenue;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.Instant;
import java.util.List;

public interface IRevenueRepository extends MongoRepository<Revenue, ObjectId> {
    List<Revenue> queryByAudit_DeletedAt(Instant auditDeletedAt);
}