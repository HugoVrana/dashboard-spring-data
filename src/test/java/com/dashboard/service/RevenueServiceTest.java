package com.dashboard.service;

import com.dashboard.common.model.Audit;
import com.dashboard.model.entities.Revenue;
import com.dashboard.repository.IRevenueRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevenueServiceTest {

    @Mock
    private IRevenueRepository revenueRepository;

    @InjectMocks
    private RevenueService revenueService;

    private Revenue testRevenue;

    @BeforeEach
    void setUp() {
        testRevenue = new Revenue();
        testRevenue.set_id(new ObjectId());
        testRevenue.setMonth("January");
        testRevenue.setRevenue(10000.0);
        testRevenue.setAudit(new Audit());
    }

    @Nested
    @DisplayName("getAllRevenues")
    class GetAllRevenuesTests {

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
}
