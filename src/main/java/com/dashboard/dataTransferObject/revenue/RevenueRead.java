package com.dashboard.dataTransferObject.revenue;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RevenueRead {
    private String id;
    private String month;
    private BigDecimal revenue;
}
