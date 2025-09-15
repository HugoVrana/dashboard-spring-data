package com.dashboard.dataTransferObject.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class InvoiceCreate {
    @NotNull(value = "Status is required")
    @NotEmpty
    public String status;

    @NotNull(value = "Amount is required")
    public Double amount;

    @NotNull(value = "Customer Id is required")
    @NotEmpty
    @JsonProperty("customer_id")
    @NotBlank(message = "customer_id is required")
    @Pattern(
            regexp = "^[a-fA-F0-9]{24}$",
            message = "customer_id must be a 24-char hex ObjectId"
    )
    public String customer_id;
}
