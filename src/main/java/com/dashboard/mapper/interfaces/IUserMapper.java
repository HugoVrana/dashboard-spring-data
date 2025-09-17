package com.dashboard.mapper.interfaces;

import com.dashboard.dataTransferObject.user.UserRead;
import com.dashboard.model.User;

public interface IUserMapper {
    UserRead toRead(User user);
}
