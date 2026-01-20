package com.dashboard.service;

import com.dashboard.common.model.Audit;
import com.dashboard.model.entities.Customer;
import com.dashboard.repository.ICustomersRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private ICustomersRepository customersRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private ObjectId testCustomerId;

    @BeforeEach
    void setUp() {
        testCustomerId = new ObjectId();
        testCustomer = new Customer();
        testCustomer.set_id(testCustomerId);
        testCustomer.setName("Acme Corp");
        testCustomer.setEmail("contact@acme.com");
        testCustomer.setImage_url("https://example.com/image.png");
        testCustomer.setAudit(new Audit());
    }

    @Nested
    @DisplayName("getAllCustomers")
    class GetAllCustomersTests {

        @Test
        @DisplayName("should return all non-deleted customers")
        void getAllCustomers_ReturnsAllNonDeletedCustomers() {
            List<Customer> expectedCustomers = List.of(testCustomer);
            when(customersRepository.findByAudit_DeletedAtIsNull()).thenReturn(expectedCustomers);

            List<Customer> result = customerService.getAllCustomers();

            assertThat(result).hasSize(1);
            assertThat(result.getFirst()).isEqualTo(testCustomer);
            verify(customersRepository).findByAudit_DeletedAtIsNull();
        }

        @Test
        @DisplayName("should return empty list when no customers exist")
        void getAllCustomers_ReturnsEmptyListWhenNoCustomers() {
            when(customersRepository.findByAudit_DeletedAtIsNull()).thenReturn(Collections.emptyList());

            List<Customer> result = customerService.getAllCustomers();

            assertThat(result).isEmpty();
            verify(customersRepository).findByAudit_DeletedAtIsNull();
        }
    }

    @Nested
    @DisplayName("getCustomer")
    class GetCustomerTests {

        @Test
        @DisplayName("should return customer when found by id")
        void getCustomer_ReturnsCustomerWhenFound() {
            when(customersRepository.findBy_idEqualsAndAudit_DeletedAtIsNull(testCustomerId))
                    .thenReturn(Optional.of(testCustomer));

            Optional<Customer> result = customerService.getCustomer(testCustomerId);

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(testCustomer);
            verify(customersRepository).findBy_idEqualsAndAudit_DeletedAtIsNull(testCustomerId);
        }

        @Test
        @DisplayName("should return empty when customer not found")
        void getCustomer_ReturnsEmptyWhenNotFound() {
            when(customersRepository.findBy_idEqualsAndAudit_DeletedAtIsNull(testCustomerId))
                    .thenReturn(Optional.empty());

            Optional<Customer> result = customerService.getCustomer(testCustomerId);

            assertThat(result).isEmpty();
            verify(customersRepository).findBy_idEqualsAndAudit_DeletedAtIsNull(testCustomerId);
        }
    }

    @Nested
    @DisplayName("getCount")
    class GetCountTests {

        @Test
        @DisplayName("should return count of non-deleted customers")
        void getCount_ReturnsCountOfNonDeletedCustomers() {
            when(customersRepository.countByAudit_DeletedAtIsNull()).thenReturn(5);

            Long result = customerService.getCount();

            assertThat(result).isEqualTo(5L);
            verify(customersRepository).countByAudit_DeletedAtIsNull();
        }

        @Test
        @DisplayName("should return zero when no customers exist")
        void getCount_ReturnsZeroWhenNoCustomers() {
            when(customersRepository.countByAudit_DeletedAtIsNull()).thenReturn(0);

            Long result = customerService.getCount();

            assertThat(result).isZero();
            verify(customersRepository).countByAudit_DeletedAtIsNull();
        }
    }
}
