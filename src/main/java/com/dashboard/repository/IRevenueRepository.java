package com.dashboard.repository;

import com.dashboard.model.entities.Revenue;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.Month;
import java.util.List;

public interface IRevenueRepository extends MongoRepository<Revenue, ObjectId> {
    List<Revenue> queryByAudit_DeletedAtIsNull();
    List<Revenue> getRevenueByYearIsAndMonthIs(Integer year, Month month);
}