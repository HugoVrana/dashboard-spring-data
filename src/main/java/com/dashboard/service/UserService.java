package com.dashboard.service;

import com.dashboard.model.entities.User;
import com.dashboard.repository.IUserRepository;
import com.dashboard.service.interfaces.IUserService;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@Scope("singleton")
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    public UserService(IUserRepository userRepository, MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<User> getAllUsers() {
        return userRepository.queryByAudit_DeletedAtIsNull();
    }

    public Optional<User> getUserById(ObjectId id) {
        return userRepository.queryUserBy_idAndAudit_DeletedAtIsNull(id);
    }

    public Page<User> searchUsers(String rawTerm, Pageable pageable) {
        if (rawTerm == null || rawTerm.isEmpty()) {
            long count = userRepository.count();

            if (pageable.isUnpaged()){
                List<User> users = userRepository.queryByAudit_DeletedAtIsNull();
                return new PageImpl<>(users, pageable, count);
            }

            Query q = new Query().with(pageable);
            q.addCriteria(Criteria.where("audit.deletedAt").is(null));

            List<User> results = mongoTemplate.find(q, User.class);
            return new PageImpl<>(results, pageable, count);
        }

        Query q = new Query().with(pageable);
        q.addCriteria(Criteria.where("audit.deletedAt").is(null));
        if (ObjectId.isValid(rawTerm)) {
            q.addCriteria(new Criteria().orOperator(
                    Criteria.where("_id").is(new ObjectId(rawTerm)),
                    Criteria.where("name").is(rawTerm),
                    Criteria.where("email").is(rawTerm)
            ));
        } else {
            q.addCriteria(Criteria.where("name").is(rawTerm)
                    .orOperator(Criteria.where("email").is(rawTerm)));
        }

        List<User> results = mongoTemplate.find(q, User.class);
        long total = mongoTemplate.count(q, User.class);
        return new PageImpl<>(results, pageable, total);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.queryUserByEmailAndAudit_DeletedAtIsNull(email);
    }
}
