package com.dashboard.repository;

import com.dashboard.model.entities.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.Instant;
import java.util.List;

public interface IUserRepository extends MongoRepository<User, ObjectId> {
    List<User> queryByAudit_DeletedAt(Instant auditDeletedAt);
}
