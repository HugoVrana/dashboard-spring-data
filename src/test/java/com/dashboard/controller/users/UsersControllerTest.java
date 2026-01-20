package com.dashboard.controller.users;

import com.dashboard.common.logging.GrafanaHttpClient;
import com.dashboard.common.model.Audit;
import com.dashboard.controller.UsersController;
import com.dashboard.dataTransferObject.page.PageRequest;
import com.dashboard.dataTransferObject.user.UserRead;
import com.dashboard.mapper.interfaces.IUserMapper;
import com.dashboard.model.entities.User;
import com.dashboard.service.interfaces.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsersController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IUserService userService;

    @MockitoBean
    private IUserMapper userMapper;

    @MockitoBean
    private GrafanaHttpClient grafanaHttpClient;

    private User testUser;
    private UserRead testUserRead;
    private ObjectId testUserId;

    @BeforeEach
    void setUp() {
        testUserId = new ObjectId();

        testUser = new User();
        testUser.set_id(testUserId);
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("password123");
        testUser.setAudit(new Audit());

        testUserRead = new UserRead();
        testUserRead.setId(testUserId.toHexString());
        testUserRead.setName("John Doe");
        testUserRead.setEmail("john.doe@example.com");
        testUserRead.setPassword("password123");
    }

    @Nested
    @DisplayName("GET /users/")
    class GetAllUsersTests {

        @Test
        @DisplayName("should return all users")
        void getAllUsers_ReturnsAllUsers() throws Exception {
            List<User> users = List.of(testUser);
            when(userService.getAllUsers()).thenReturn(users);
            when(userMapper.toRead(testUser)).thenReturn(testUserRead);

            mockMvc.perform(get("/users/"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(testUserId.toHexString()))
                    .andExpect(jsonPath("$[0].name").value("John Doe"))
                    .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));
        }

        @Test
        @DisplayName("should return empty list when no users exist")
        void getAllUsers_ReturnsEmptyListWhenNoUsers() throws Exception {
            when(userService.getAllUsers()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/users/"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /users/{id}")
    class GetUserByIdTests {

        @Test
        @DisplayName("should return user when found")
        void getUserById_ReturnsUserWhenFound() throws Exception {
            when(userService.getUserById(testUserId)).thenReturn(Optional.of(testUser));
            when(userMapper.toRead(testUser)).thenReturn(testUserRead);

            mockMvc.perform(get("/users/{id}", testUserId.toHexString()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testUserId.toHexString()))
                    .andExpect(jsonPath("$.name").value("John Doe"))
                    .andExpect(jsonPath("$.email").value("john.doe@example.com"));
        }

        @Test
        @DisplayName("should return 404 when user not found")
        void getUserById_Returns404WhenNotFound() throws Exception {
            when(userService.getUserById(testUserId)).thenReturn(Optional.empty());

            mockMvc.perform(get("/users/{id}", testUserId.toHexString()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return 404 when id is invalid")
        void getUserById_Returns404WhenIdInvalid() throws Exception {
            mockMvc.perform(get("/users/{id}", "invalid-id"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /users/email/{email}")
    class GetUserByEmailTests {

        @Test
        @DisplayName("should return user when found by email")
        void getUserByEmail_ReturnsUserWhenFound() throws Exception {
            String email = "john.doe@example.com";
            when(userService.getUserByEmail(email)).thenReturn(Optional.of(testUser));
            when(userMapper.toRead(testUser)).thenReturn(testUserRead);

            mockMvc.perform(get("/users/email/{email}", email))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.email").value(email))
                    .andExpect(jsonPath("$.name").value("John Doe"));
        }

        @Test
        @DisplayName("should return 404 when user not found by email")
        void getUserByEmail_Returns404WhenNotFound() throws Exception {
            String email = "nonexistent@example.com";
            when(userService.getUserByEmail(email)).thenReturn(Optional.empty());

            mockMvc.perform(get("/users/email/{email}", email))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /users/search")
    class SearchUsersTests {

        @Test
        @DisplayName("should return paginated results when search succeeds")
        void searchUsers_ReturnsPaginatedResultsWhenSucceeds() throws Exception {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(1);
            pageRequest.setSize(10);
            pageRequest.setSearch("John");

            Page<User> userPage = new PageImpl<>(List.of(testUser), Pageable.ofSize(10), 1);
            when(userService.searchUsers(eq("John"), any(Pageable.class))).thenReturn(userPage);
            when(userMapper.toRead(testUser)).thenReturn(testUserRead);

            mockMvc.perform(post("/users/search")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pageRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data[0].name").value("John Doe"))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.currentPage").value(1));
        }

        @Test
        @DisplayName("should return 204 when no results found")
        void searchUsers_Returns204WhenNoResults() throws Exception {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(1);
            pageRequest.setSize(10);
            pageRequest.setSearch("nonexistent");

            Page<User> emptyPage = Page.empty();
            when(userService.searchUsers(eq("nonexistent"), any(Pageable.class))).thenReturn(emptyPage);

            mockMvc.perform(post("/users/search")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pageRequest)))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("should use unpaged when page is null")
        void searchUsers_UsesUnpagedWhenPageIsNull() throws Exception {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(null);
            pageRequest.setSize(10);
            pageRequest.setSearch("John");

            Page<User> userPage = new PageImpl<>(List.of(testUser));
            when(userService.searchUsers(eq("John"), any(Pageable.class))).thenReturn(userPage);
            when(userMapper.toRead(testUser)).thenReturn(testUserRead);

            mockMvc.perform(post("/users/search")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pageRequest)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("should use unpaged when page is zero")
        void searchUsers_UsesUnpagedWhenPageIsZero() throws Exception {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(0);
            pageRequest.setSize(10);
            pageRequest.setSearch("John");

            Page<User> userPage = new PageImpl<>(List.of(testUser));
            when(userService.searchUsers(eq("John"), any(Pageable.class))).thenReturn(userPage);
            when(userMapper.toRead(testUser)).thenReturn(testUserRead);

            mockMvc.perform(post("/users/search")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pageRequest)))
                    .andExpect(status().isOk());
        }
    }
}
