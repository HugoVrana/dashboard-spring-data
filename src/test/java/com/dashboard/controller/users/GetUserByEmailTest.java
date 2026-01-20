package com.dashboard.controller.users;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("GET /users/email/{email}")
public class GetUserByEmailTest extends BaseUsersControllerTest {

    @Test
    @DisplayName("should return user when found by email")
    void getUserByEmail_ReturnsUserWhenFound() throws Exception {
        when(userService.getUserByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userMapper.toRead(testUser)).thenReturn(testUserRead);

        mockMvc.perform(get("/users/email/{email}", testEmail))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(testEmail))
                .andExpect(jsonPath("$.name").value(testName));
    }

    @Test
    @DisplayName("should return 404 when user not found by email")
    void getUserByEmail_Returns404WhenNotFound() throws Exception {
        String nonExistentEmail = "nonexistent@example.com";
        when(userService.getUserByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/email/{email}", nonExistentEmail))
                .andExpect(status().isNotFound());
    }
}
