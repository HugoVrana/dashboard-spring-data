package com.dashboard.dataTransferObjects;

import com.dashboard.model.Customer;
import lombok.Data;

@Data
public class CustomerDto {
    public String id;
    public String name;
    public String email;
    public String image_url;

    public CustomerDto(Customer customer) {
        this.id = customer.get_id().toHexString();
        this.name = customer.getName();
        this.email = customer.getEmail();
        this.image_url = customer.getImage_url();
    }
}