package com.dashboard.dataTransferObject.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CustomerRead {
    private String id;
    private String name;
    private String email;
    @JsonProperty("image_url")
    private String imageUrl;
}