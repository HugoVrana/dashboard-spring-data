package com.dashboard.repository;

import com.dashboard.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IUserRepository extends MongoRepository<User, ObjectId> {
}
