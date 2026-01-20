package com.dashboard.service.user;

import com.dashboard.model.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("getUserById")
public class GetUserByIdTest extends BaseUserServiceTest {

    @Test
    @DisplayName("should return user when found by id")
    void getUserById_ReturnsUserWhenFound() {
        when(userRepository.queryUserBy_idAndAudit_DeletedAtIsNull(testUserId))
                .thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById(testUserId);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testUser);
        verify(userRepository).queryUserBy_idAndAudit_DeletedAtIsNull(testUserId);
    }

    @Test
    @DisplayName("should return empty when user not found")
    void getUserById_ReturnsEmptyWhenNotFound() {
        when(userRepository.queryUserBy_idAndAudit_DeletedAtIsNull(testUserId))
                .thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(testUserId);

        assertThat(result).isEmpty();
        verify(userRepository).queryUserBy_idAndAudit_DeletedAtIsNull(testUserId);
    }
}
