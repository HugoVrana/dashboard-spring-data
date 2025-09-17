package com.dashboard.dataTransferObject.user;

import lombok.Data;

@Data
public class UserRead {
    public String id;
    public String name;
    public String email;
    public String password;
}