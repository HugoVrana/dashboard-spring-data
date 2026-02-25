package com.dashboard.dataTransferObject.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceCreate {
    @NotNull(value = "Status is required")
    @NotEmpty
    private String status;

    @NotNull(value = "Amount is required")
    private BigDecimal amount;

    @NotNull(value = "Customer Id is required")
    @NotEmpty
    @JsonProperty("customer_id")
    @NotBlank(message = "customer_id is required")
    @Pattern(
            regexp = "^[a-fA-F0-9]{24}$",
            message = "customer_id must be a 24-char hex ObjectId"
    )
    private String customerId;
}
