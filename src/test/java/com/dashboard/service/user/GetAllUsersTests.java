package com.dashboard.service.user;

import com.dashboard.model.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetAllUsersTests extends BaseUserServiceTest {
    @Test
    @DisplayName("should return all non-deleted users")
    void getAllUsers_ReturnsAllNonDeletedUsers() {
        List<User> expectedUsers = List.of(testUser);
        when(userRepository.queryByAudit_DeletedAtIsNull()).thenReturn(expectedUsers);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(testUser);
        verify(userRepository).queryByAudit_DeletedAtIsNull();
    }

    @Test
    @DisplayName("should return empty list when no users exist")
    void getAllUsers_ReturnsEmptyListWhenNoUsers() {
        when(userRepository.queryByAudit_DeletedAtIsNull()).thenReturn(Collections.emptyList());

        List<User> result = userService.getAllUsers();

        assertThat(result).isEmpty();
        verify(userRepository).queryByAudit_DeletedAtIsNull();
    }
}
