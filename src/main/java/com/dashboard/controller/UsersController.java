package com.dashboard.controller;

import com.dashboard.common.model.exception.ResourceNotFoundException;
import com.dashboard.dataTransferObject.page.PageRead;
import com.dashboard.dataTransferObject.page.PageRequest;
import com.dashboard.dataTransferObject.user.UserRead;
import com.dashboard.mapper.interfaces.IUserMapper;
import com.dashboard.model.entities.User;
import com.dashboard.service.interfaces.IUserService;
import jakarta.validation.constraints.Email;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/users")
@lombok.RequiredArgsConstructor
public class UsersController {

    private final IUserService userService;
    private final IUserMapper userMapper;

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

    @GetMapping("/{id}")
    public ResponseEntity<UserRead> getUserById(@PathVariable("id") String id) {
        if (!ObjectId.isValid(id)) {
            throw new ResourceNotFoundException("Invalid id");
        }

        ObjectId userId = new ObjectId(id);
        Optional<User> optionalUser = userService.getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }

        User user = optionalUser.get();
        UserRead userRead = userMapper.toRead(user);
        return ResponseEntity.ok(userRead);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserRead> getUserByEmail(@PathVariable("email") @Email String email) {
        Optional<User> optionalUser = userService.getUserByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User with email " + email + " not found");
        }
        User user = optionalUser.get();
        UserRead userRead = userMapper.toRead(user);
        return ResponseEntity.ok(userRead);
    }

    @PostMapping(value = "/search", consumes = "application/json")
    public ResponseEntity<PageRead<UserRead>> searchUsers(@RequestBody PageRequest pageRequest) {
        Pageable pageable;
        if (pageRequest.getPage() == null || pageRequest.getPage() < 1) {
            pageable = Pageable.unpaged();
        } else {
            pageable = Pageable.ofSize(pageRequest.getSize()).withPage(pageRequest.getPage() - 1);
        }

        Page<User> users = userService.searchUsers(pageRequest.getSearch(), pageable);

        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        PageRead<UserRead> pageRead = new PageRead<>();
        List<User> content = users.stream().toList();
        List<UserRead> userReads = new ArrayList<>();
        for(User user : content) {
            UserRead userRead = userMapper.toRead(user);
            userReads.add(userRead);
        }

        pageRead.setData(userReads);
        pageRead.setTotalPages(users.getTotalPages());
        pageRead.setItemsPerPage(users.getSize());
        pageRead.setCurrentPage(users.getNumber() + 1);
        return ResponseEntity.ok(pageRead);
    }
}
