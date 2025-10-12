package com.dashboard.mapper;

import com.dashboard.dataTransferObject.revenue.RevenueRead;
import com.dashboard.mapper.interfaces.IRevenueMapper;
import com.dashboard.model.entities.Revenue;
import org.springframework.stereotype.Service;

@Service
public class RevenueMapper implements IRevenueMapper {
    @Override
    public RevenueRead toRead(Revenue revenue) {
        RevenueRead revenueRead = new RevenueRead();
        revenueRead.setId(revenue.get_id().toHexString());
        revenueRead.setMonth(revenue.getMonth());
        revenueRead.setRevenue(revenue.getRevenue());
        return revenueRead;
    }
}
