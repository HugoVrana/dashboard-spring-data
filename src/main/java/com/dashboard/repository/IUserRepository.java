package com.dashboard.repository;

import com.dashboard.model.entities.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface IUserRepository extends MongoRepository<User, ObjectId> {
    List<User> queryByAudit_DeletedAtIsNull();
    Optional<User> queryUserBy_idAndAudit_DeletedAtIsNull(ObjectId id);
}