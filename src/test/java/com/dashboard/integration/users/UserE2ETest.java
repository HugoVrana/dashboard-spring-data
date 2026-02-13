package com.dashboard.integration.users;

import com.dashboard.dataTransferObject.page.PageRequest;
import com.dashboard.integration.BaseIntegrationTest;
import com.dashboard.model.entities.User;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * E2E integration tests for User endpoints.
 */
@Feature("User E2E")
@DisplayName("User E2E Tests")
public class UserE2ETest extends BaseIntegrationTest {

    @Test
    @Story("Get All Users")
    @DisplayName("GET /users/ returns all active users")
    void getAllUsers_ReturnsAllActiveUsers() throws Exception {
        User user1 = createAndSaveUser();
        User user2 = createAndSaveUser();

        mockMvc.perform(get("/users/")
                        .header("Authorization", authHeader("dashboard-users-read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(
                        user1.get_id().toHexString(),
                        user2.get_id().toHexString()
                )));
    }

    @Test
    @Story("Get User By ID")
    @DisplayName("GET /users/{id} retrieves by ID")
    void getUserById_RetrievesById() throws Exception {
        User user = createAndSaveUser();

        mockMvc.perform(get("/users/" + user.get_id().toHexString())
                        .header("Authorization", authHeader("dashboard-users-read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.get_id().toHexString()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    @Story("Get User By Email")
    @DisplayName("GET /users/email/{email} retrieves by email")
    void getUserByEmail_RetrievesByEmail() throws Exception {
        User user = createAndSaveUser();

        mockMvc.perform(get("/users/email/" + user.getEmail())
                        .header("Authorization", authHeader("dashboard-users-read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.get_id().toHexString()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    @Story("Search Users")
    @DisplayName("POST /users/search with pagination returns results")
    void searchUsers_WithPagination_ReturnsResults() throws Exception {
        createAndSaveUser();
        createAndSaveUser();

        PageRequest pageRequest = new PageRequest();
        pageRequest.setSearch("");
        pageRequest.setPage(1);
        pageRequest.setSize(10);

        mockMvc.perform(post("/users/search")
                        .header("Authorization", authHeader("dashboard-users-create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(1));
    }

    @Test
    @Story("Soft Delete Exclusion")
    @DisplayName("GET /users/ excludes soft-deleted users")
    void getAllUsers_ExcludesSoftDeleted() throws Exception {
        User activeUser = createAndSaveUser();

        // Create and soft-delete a user
        User deletedUser = createAndSaveUser();
        deletedUser.setAudit(createDeletedAudit());
        userRepository.save(deletedUser);

        mockMvc.perform(get("/users/")
                        .header("Authorization", authHeader("dashboard-users-read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(activeUser.get_id().toHexString()));
    }

    @Test
    @Story("User Not Found")
    @DisplayName("GET /users/{id} returns 404 for non-existent user")
    void getUserById_Returns404ForNonExistent() throws Exception {
        mockMvc.perform(get("/users/507f1f77bcf86cd799439011")
                        .header("Authorization", authHeader("dashboard-users-read")))
                .andExpect(status().isNotFound());
    }

    @Test
    @Story("User Email Not Found")
    @DisplayName("GET /users/email/{email} returns 404 for non-existent email")
    void getUserByEmail_Returns404ForNonExistent() throws Exception {
        mockMvc.perform(get("/users/email/nonexistent@example.com")
                        .header("Authorization", authHeader("dashboard-users-read")))
                .andExpect(status().isNotFound());
    }

    @Test
    @Story("Invalid ID")
    @DisplayName("GET /users/{id} returns 404 for invalid ID format")
    void getUserById_Returns404ForInvalidId() throws Exception {
        mockMvc.perform(get("/users/invalid-id")
                        .header("Authorization", authHeader("dashboard-users-read")))
                .andExpect(status().isNotFound());
    }
}
