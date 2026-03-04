package com.dashboard.dataTransferObject.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreate {
    @NotEmpty
    @NotNull(value = "Id is required")
    private String name;

    @Email
    @NotEmpty
    @NotNull(value = "Email is required")
    private String email;

    @URL
    private String imageUrl;
}
