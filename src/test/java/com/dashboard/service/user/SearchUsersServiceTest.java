package com.dashboard.service.user;

import com.dashboard.model.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("UserService - searchUsers")
class SearchUsersServiceTest extends BaseUserServiceTest {

    @Test
    @DisplayName("should return all users when search term is null")
    void searchUsers_ReturnsAllUsersWhenTermIsNull() {
        Pageable pageable = Pageable.unpaged();
        List<User> users = List.of(testUser);
        when(userRepository.count()).thenReturn(1L);
        when(userRepository.queryByAudit_DeletedAtIsNull()).thenReturn(users);

        Page<User> result = userService.searchUsers(null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(userRepository).queryByAudit_DeletedAtIsNull();
    }

    @Test
    @DisplayName("should return all users when search term is empty")
    void searchUsers_ReturnsAllUsersWhenTermIsEmpty() {
        Pageable pageable = Pageable.unpaged();
        List<User> users = List.of(testUser);
        when(userRepository.count()).thenReturn(1L);
        when(userRepository.queryByAudit_DeletedAtIsNull()).thenReturn(users);

        Page<User> result = userService.searchUsers("", pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(userRepository).queryByAudit_DeletedAtIsNull();
    }

    @Test
    @DisplayName("should use pagination when page is specified")
    void searchUsers_UsesPaginationWhenPageSpecified() {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = List.of(testUser);
        when(userRepository.count()).thenReturn(1L);
        when(mongoTemplate.find(any(Query.class), eq(User.class))).thenReturn(users);

        Page<User> result = userService.searchUsers(null, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(mongoTemplate).find(any(Query.class), eq(User.class));
    }

    @Test
    @DisplayName("should search by ObjectId when term is valid ObjectId")
    void searchUsers_SearchesByObjectIdWhenTermIsValidObjectId() {
        Pageable pageable = PageRequest.of(0, 10);
        String objectIdTerm = testUserId.toHexString();
        List<User> users = List.of(testUser);
        when(mongoTemplate.find(any(Query.class), eq(User.class))).thenReturn(users);
        when(mongoTemplate.count(any(Query.class), eq(User.class))).thenReturn(1L);

        Page<User> result = userService.searchUsers(objectIdTerm, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(mongoTemplate).find(any(Query.class), eq(User.class));
    }

    @Test
    @DisplayName("should search by name or email when term is not ObjectId")
    void searchUsers_SearchesByNameOrEmailWhenTermIsNotObjectId() {
        Pageable pageable = PageRequest.of(0, 10);
        String searchTerm = "john";
        List<User> users = List.of(testUser);
        when(mongoTemplate.find(any(Query.class), eq(User.class))).thenReturn(users);
        when(mongoTemplate.count(any(Query.class), eq(User.class))).thenReturn(1L);

        Page<User> result = userService.searchUsers(searchTerm, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(mongoTemplate).find(any(Query.class), eq(User.class));
        verify(mongoTemplate).count(any(Query.class), eq(User.class));
    }

    @Test
    @DisplayName("should return empty page when no results found")
    void searchUsers_ReturnsEmptyPageWhenNoResults() {
        Pageable pageable = PageRequest.of(0, 10);
        String searchTerm = "nonexistent";
        when(mongoTemplate.find(any(Query.class), eq(User.class))).thenReturn(Collections.emptyList());
        when(mongoTemplate.count(any(Query.class), eq(User.class))).thenReturn(0L);

        Page<User> result = userService.searchUsers(searchTerm, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
}
