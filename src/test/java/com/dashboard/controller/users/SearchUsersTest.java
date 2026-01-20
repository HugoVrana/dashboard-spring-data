package com.dashboard.controller.users;

import com.dashboard.dataTransferObject.page.PageRequest;
import com.dashboard.model.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("POST /users/search")
public class SearchUsersTest extends BaseUsersControllerTest {

    @Test
    @DisplayName("should return paginated results when search succeeds")
    void searchUsers_ReturnsPaginatedResultsWhenSucceeds() throws Exception {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(1);
        pageRequest.setSize(10);
        pageRequest.setSearch(testName);

        Page<User> userPage = new PageImpl<>(List.of(testUser), Pageable.ofSize(10), 1);
        when(userService.searchUsers(eq(testName), any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toRead(testUser)).thenReturn(testUserRead);

        mockMvc.perform(post("/users/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data[0].name").value(testName))
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
        pageRequest.setSearch(testName);

        Page<User> userPage = new PageImpl<>(List.of(testUser));
        when(userService.searchUsers(eq(testName), any(Pageable.class))).thenReturn(userPage);
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
        pageRequest.setSearch(testName);

        Page<User> userPage = new PageImpl<>(List.of(testUser));
        when(userService.searchUsers(eq(testName), any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toRead(testUser)).thenReturn(testUserRead);

        mockMvc.perform(post("/users/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isOk());
    }
}
