package com.dashboard.repository;

import com.dashboard.model.entities.OAuthClientDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface IOAuthClientRepository extends MongoRepository<OAuthClientDocument, ObjectId> {
    Optional<OAuthClientDocument> findBy_idAndAudit_DeletedAtIsNull(ObjectId id);
}
