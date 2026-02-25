package com.dashboard.dataTransferObject.invoice;

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
public class InvoiceUpdate {
    @NotNull(value = "Id is required")
    @NotEmpty
    private String id;

    @NotNull(value = "Status is required")
    @NotEmpty
    private String status;

    @NotNull(value = "Amount is required")
    private BigDecimal amount;

    @NotNull(value = "Customer Id is required")
    @NotEmpty
    @NotBlank(message = "customerId is required")
    @Pattern(
            regexp = "^[a-fA-F0-9]{24}$",
            message = "customerId must be a 24-char hex ObjectId"
    )
    private String customerId;
}
