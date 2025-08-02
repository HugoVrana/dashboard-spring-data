package com.dashboard.repository;

import com.dashboard.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.UUID;

public interface IUserRepository extends MongoRepository<User, UUID> {
}
