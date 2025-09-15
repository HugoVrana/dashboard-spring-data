package com.dashboard.dataTransferObject.customer;

import lombok.Data;

@Data
public class CustomerRead {
    public String id;
    public String name;
    public String email;
    public String image_url;
}