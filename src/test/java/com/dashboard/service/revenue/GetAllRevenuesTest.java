package com.dashboard.service.revenue;

import com.dashboard.model.entities.Revenue;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Story("Get All Revenues")
@DisplayName("getAllRevenues")
public class GetAllRevenuesTest extends BaseRevenueServiceTest {
    @Test
    @DisplayName("should return all non-deleted revenues")
    void getAllRevenues_ReturnsAllNonDeletedRevenues() {
        List<Revenue> expectedRevenues = List.of(testRevenue);
        when(revenueRepository.queryByAudit_DeletedAtIsNull()).thenReturn(expectedRevenues);

        List<Revenue> result = revenueService.getAllRevenues();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(testRevenue);
        verify(revenueRepository).queryByAudit_DeletedAtIsNull();
    }

    @Test
    @DisplayName("should return empty list when no revenues exist")
    void getAllRevenues_ReturnsEmptyListWhenNoRevenues() {
        when(revenueRepository.queryByAudit_DeletedAtIsNull()).thenReturn(Collections.emptyList());

        List<Revenue> result = revenueService.getAllRevenues();

        assertThat(result).isEmpty();
        verify(revenueRepository).queryByAudit_DeletedAtIsNull();
    }
}
