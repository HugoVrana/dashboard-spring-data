package com.dashboard.service;

import com.dashboard.model.entities.Revenue;
import com.dashboard.repository.IRevenueRepository;
import com.dashboard.service.interfaces.IRevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Scope("singleton")
@RequiredArgsConstructor
public class RevenueService implements IRevenueService {
    private final IRevenueRepository revenueRepository;

    public List<Revenue> getAllRevenues() {
        return revenueRepository.queryByAudit_DeletedAtIsNull();
    }
}
