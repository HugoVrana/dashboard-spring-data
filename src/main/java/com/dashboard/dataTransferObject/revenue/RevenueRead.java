package com.dashboard.dataTransferObject.revenue;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RevenueRead {
    private String id;
    private String month;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal revenue;
}
