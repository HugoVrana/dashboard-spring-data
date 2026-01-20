package com.dashboard.controller.users;

import com.dashboard.common.logging.GrafanaHttpClient;
import com.dashboard.common.model.Audit;
import com.dashboard.controller.UsersController;
import com.dashboard.dataTransferObject.user.UserRead;
import com.dashboard.mapper.interfaces.IUserMapper;
import com.dashboard.model.entities.User;
import com.dashboard.service.interfaces.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UsersController.class)
@AutoConfigureMockMvc(addFilters = false)
public abstract class BaseUsersControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected IUserService userService;

    @MockitoBean
    protected IUserMapper userMapper;

    @MockitoBean
    protected GrafanaHttpClient grafanaHttpClient;

    protected final Faker faker = new Faker();

    protected ObjectId testUserId;
    protected User testUser;
    protected UserRead testUserRead;

    protected String testName;
    protected String testEmail;
    protected String testPassword;

    @BeforeEach
    void setUpBase() {
        testUserId = new ObjectId();
        testName = faker.name().fullName();
        testEmail = faker.internet().emailAddress();
        testPassword = faker.internet().password();

        testUser = new User();
        testUser.set_id(testUserId);
        testUser.setName(testName);
        testUser.setEmail(testEmail);
        testUser.setPassword(testPassword);
        testUser.setAudit(new Audit());

        testUserRead = new UserRead();
        testUserRead.setId(testUserId.toHexString());
        testUserRead.setName(testName);
        testUserRead.setEmail(testEmail);
        testUserRead.setPassword(testPassword);
    }
}
