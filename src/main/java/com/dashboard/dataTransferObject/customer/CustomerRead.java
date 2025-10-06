package com.dashboard.dataTransferObject.customer;

import lombok.Data;

@Data
public class CustomerRead {
    private String id;
    private String name;
    private String email;
    private String image_url;
}