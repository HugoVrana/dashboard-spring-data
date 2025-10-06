package com.dashboard.controller;

import com.dashboard.dataTransferObject.user.UserRead;
import com.dashboard.mapper.interfaces.IUserMapper;
import com.dashboard.model.User;
import com.dashboard.service.interfaces.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UsersController {

    private final IUserService userService;
    private final IUserMapper userMapper;

    public UsersController(IUserService userService, IUserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/")
    public ResponseEntity<List<UserRead>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserRead> userReads = new ArrayList<>();
        for(User user : users) {
            UserRead userRead = userMapper.toRead(user);
            userReads.add(userRead);
        }
        return ResponseEntity.ok(userReads);
    }
}
