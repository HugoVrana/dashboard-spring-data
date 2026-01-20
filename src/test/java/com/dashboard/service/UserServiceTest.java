package com.dashboard.service;

import com.dashboard.common.model.Audit;
import com.dashboard.model.entities.User;
import com.dashboard.repository.IUserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private UserService userService;

    private User testUser;
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
    }

    @Nested
    @DisplayName("getAllUsers")
    class GetAllUsersTests {

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

    @Nested
    @DisplayName("getUserById")
    class GetUserByIdTests {

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

    @Nested
    @DisplayName("getUserByEmail")
    class GetUserByEmailTests {

        @Test
        @DisplayName("should return user when found by email")
        void getUserByEmail_ReturnsUserWhenFound() {
            String email = "john.doe@example.com";
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

    @Nested
    @DisplayName("searchUsers")
    class SearchUsersTests {

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
}
