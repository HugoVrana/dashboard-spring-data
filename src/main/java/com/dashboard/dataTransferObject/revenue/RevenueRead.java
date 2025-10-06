package com.dashboard.dataTransferObject.revenue;

import lombok.Data;

@Data
public class RevenueRead {
    private String id;
    private String month;
    private Double revenue;
}
