package com.dashboard.mapper;

import com.dashboard.dataTransferObject.user.UserRead;
import com.dashboard.mapper.interfaces.IUserMapper;
import com.dashboard.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper implements IUserMapper {
    @Override
    public UserRead toRead(User user) {
        UserRead userRead = new UserRead();
        userRead.setId(user.get_id().toHexString());
        userRead.setEmail(user.getEmail());
        userRead.setName(user.getName());
        userRead.setPassword(user.getPassword());
        return userRead;
    }
}