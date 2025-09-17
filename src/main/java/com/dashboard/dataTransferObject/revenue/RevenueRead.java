package com.dashboard.dataTransferObject.revenue;

import lombok.Data;

@Data
public class RevenueRead {
    public String id;
    public String month;
    public Double revenue;
}
