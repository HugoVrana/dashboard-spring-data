package com.dashboard.service.user;

import com.dashboard.common.model.Audit;
import com.dashboard.model.entities.User;
import com.dashboard.repository.IUserRepository;
import com.dashboard.service.UserService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

@Tag("service-user")
@ExtendWith(MockitoExtension.class)
public abstract class BaseUserServiceTest {

    @Mock
    protected IUserRepository userRepository;

    @Mock
    protected MongoTemplate mongoTemplate;

    protected User testUser;
    protected ObjectId testUserId;

    @InjectMocks
    protected UserService userService;

    @BeforeEach
    void setUp() {
        testUserId = new ObjectId();
        testUser = new User();
        testUser.set_id(testUserId);
        testUser.setName("User");
        testUser.setEmail("user@nextmail.com");
        testUser.setPassword("123456");
        testUser.setAudit(new Audit());
    }
}
