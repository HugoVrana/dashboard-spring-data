package com.dashboard.service.customer;

import com.dashboard.model.entities.Customer;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Story("Get All Customers")
@DisplayName("getAllCustomers")
public class GetAllCustomersTest extends BaseCustomerServiceTest {

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
