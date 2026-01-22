package com.dashboard.service.customer;

import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Story("Get Customer Count")
@DisplayName("getCount")
public class GetCountTest extends BaseCustomerServiceTest {

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
