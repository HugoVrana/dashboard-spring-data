package com.dashboard.dataTransferObject.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class InvoiceUpdate {
    @NotNull(value = "Id is required")
    @NotEmpty
    private String id;

    @NotNull(value = "Status is required")
    @NotEmpty
    private String status;

    @NotNull(value = "Amount is required")
    private Double amount;

    @NotNull(value = "Customer Id is required")
    @NotEmpty
    @NotBlank(message = "customerId is required")
    @Pattern(
            regexp = "^[a-fA-F0-9]{24}$",
            message = "customerId must be a 24-char hex ObjectId"
    )
    private String customerId;
}
