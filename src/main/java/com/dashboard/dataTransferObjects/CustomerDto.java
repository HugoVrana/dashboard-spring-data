package com.dashboard.dataTransferObjects;

import lombok.Data;

@Data
public class CustomerDto {
    public String id;
    public String name;
    public String email;
    public String image_url;
}