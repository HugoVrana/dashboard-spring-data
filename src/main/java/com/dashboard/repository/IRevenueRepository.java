package com.dashboard.repository;

import com.dashboard.model.Revenue;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IRevenueRepository extends MongoRepository<Revenue, ObjectId> {
}