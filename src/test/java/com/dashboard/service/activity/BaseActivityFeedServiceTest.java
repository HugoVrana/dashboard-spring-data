package com.dashboard.service.activity;

import com.dashboard.common.model.ActivityEvent;
import com.dashboard.service.ActivityFeedService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Epic("Activity Feed")
@Feature("Activity Feed Service")
@Tag("service-activity")
@ExtendWith(MockitoExtension.class)
public abstract class BaseActivityFeedServiceTest {

    @Mock
    protected SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    protected ActivityFeedService activityFeedService;

    protected ActivityEvent testEvent;

    @BeforeEach
    void setUp() {
        testEvent = ActivityEvent.builder()
                .id(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .type("INVOICE_CREATED")
                .actorId("testUser")
                .metadata(Map.of("invoiceId", "123", "amount", 100.0))
                .build();
    }

    protected ActivityEvent createTestEvent(String type) {
        return ActivityEvent.builder()
                .id(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .type(type)
                .actorId("testUser")
                .metadata(Map.of("invoiceId", UUID.randomUUID().toString()))
                .build();
    }
}
