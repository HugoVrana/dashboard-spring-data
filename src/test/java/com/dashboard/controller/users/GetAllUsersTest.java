package com.dashboard.controller.users;

import com.dashboard.model.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("GET /users/")
public class GetAllUsersTest extends BaseUsersControllerTest {

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
                .andExpect(jsonPath("$[0].name").value(testName))
                .andExpect(jsonPath("$[0].email").value(testEmail));
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
