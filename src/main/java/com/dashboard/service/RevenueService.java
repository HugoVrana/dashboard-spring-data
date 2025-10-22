package com.dashboard.service;

import com.dashboard.model.entities.Revenue;
import com.dashboard.repository.IRevenueRepository;
import com.dashboard.service.interfaces.IRevenueService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Scope("singleton")
public class RevenueService implements IRevenueService {
    private final IRevenueRepository revenueRepository;

    public RevenueService(IRevenueRepository revenueRepository) {
        this.revenueRepository = revenueRepository;
    }

    public List<Revenue> getAllRevenues() {
        return revenueRepository.queryByAudit_DeletedAtIsNull();
    }
}
