package com.dashboard.dataTransferObject.user;

import lombok.Data;

@Data
public class UserRead {
    private String id;
    private String name;
    private String email;
    private String password;
}