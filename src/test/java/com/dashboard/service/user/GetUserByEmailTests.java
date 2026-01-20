package com.dashboard.service.user;

import com.dashboard.model.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName( "getUserByEmail")
public class GetUserByEmailTests extends BaseUserServiceTest {

    @Test
    @DisplayName("should return user when found by email")
    void getUserByEmail_ReturnsUserWhenFound() {
        String email = "user@nextmail.com";
        when(userRepository.queryUserByEmailAndAudit_DeletedAtIsNull(email))
                .thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserByEmail(email);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
        verify(userRepository).queryUserByEmailAndAudit_DeletedAtIsNull(email);
    }

    @Test
    @DisplayName("should return empty when user not found by email")
    void getUserByEmail_ReturnsEmptyWhenNotFound() {
        String email = "nonexistent@example.com";
        when(userRepository.queryUserByEmailAndAudit_DeletedAtIsNull(email))
                .thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByEmail(email);

        assertThat(result).isEmpty();
        verify(userRepository).queryUserByEmailAndAudit_DeletedAtIsNull(email);
    }
}
