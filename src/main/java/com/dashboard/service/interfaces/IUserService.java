package com.dashboard.service.interfaces;

import com.dashboard.model.entities.User;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> getAllUsers();
    Optional<User> getUserById(ObjectId id);
    Page<User> searchUsers(String rawTerm, Pageable pageable);
}
