package com.dashboard.service;

import com.dashboard.model.entities.User;
import com.dashboard.repository.IUserRepository;
import com.dashboard.service.interfaces.IUserService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Scope("singleton")
public class UserService implements IUserService {
    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.queryByAudit_DeletedAt(null);
    }
}
