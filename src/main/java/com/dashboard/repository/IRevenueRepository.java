package com.dashboard.repository;

import com.dashboard.model.Revenue;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.UUID;

public interface IRevenueRepository extends MongoRepository<Revenue, UUID> {
}
