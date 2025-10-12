package com.dashboard.mapper.interfaces;

import com.dashboard.dataTransferObject.revenue.RevenueRead;
import com.dashboard.model.entities.Revenue;

public interface IRevenueMapper {
    RevenueRead toRead(Revenue revenue);
}
