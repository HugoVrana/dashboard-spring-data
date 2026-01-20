package com.dashboard.service.customer;

import com.dashboard.model.entities.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("CustomerService - getCustomer")
class GetCustomerServiceTest extends BaseCustomerServiceTest {

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
