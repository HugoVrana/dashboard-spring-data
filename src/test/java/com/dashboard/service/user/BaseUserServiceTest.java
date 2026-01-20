package com.dashboard.service.user;

import com.dashboard.common.model.Audit;
import com.dashboard.model.entities.User;
import com.dashboard.repository.IUserRepository;
import com.dashboard.service.UserService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ExtendWith(MockitoExtension.class)
public abstract class BaseUserServiceTest {

    @MockitoBean
    protected IUserRepository userRepository;

    @MockitoBean
    private MongoTemplate mongoTemplate;

    protected User testUser;
    protected ObjectId testUserId;

    @MockitoBean
    protected UserService userService;

    @BeforeEach
    void setUp() {
        testUserId = new ObjectId();
        testUser = new User();
        testUser.set_id(testUserId);
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("password123");
        testUser.setAudit(new Audit());
    }
}
