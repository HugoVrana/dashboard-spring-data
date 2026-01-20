package com.dashboard.controller.users;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("GET /users/{id}")
public class GetUserByIdTest extends BaseUsersControllerTest {

    @Test
    @DisplayName("should return user when found")
    void getUserById_ReturnsUserWhenFound() throws Exception {
        when(userService.getUserById(testUserId)).thenReturn(Optional.of(testUser));
        when(userMapper.toRead(testUser)).thenReturn(testUserRead);

        mockMvc.perform(get("/users/{id}", testUserId.toHexString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testUserId.toHexString()))
                .andExpect(jsonPath("$.name").value(testName))
                .andExpect(jsonPath("$.email").value(testEmail));
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
