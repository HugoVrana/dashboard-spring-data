package com.dashboard.mapper.interfaces;

import com.dashboard.dataTransferObject.user.UserRead;
import com.dashboard.model.entities.User;

public interface IUserMapper {
    UserRead toRead(User user);
}
