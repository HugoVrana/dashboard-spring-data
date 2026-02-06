package com.dashboard.service.activity;

import com.dashboard.common.model.ActivityEvent;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.verify;

@Story("Publish Activity Event")
class PublishEventTest extends BaseActivityFeedServiceTest {

    @Test
    @DisplayName("Should publish event to WebSocket topic")
    @Description("Verifies that publishing an event sends it to the /topic/activity WebSocket destination")
    void shouldPublishEventToWebSocket() {
        activityFeedService.publishEvent(testEvent);

        verify(messagingTemplate).convertAndSend("/topic/activity", testEvent);
    }

    @Test
    @DisplayName("Should add event to recent events")
    @Description("Verifies that publishing an event adds it to the in-memory recent events list")
    void shouldAddEventToRecentEvents() {
        activityFeedService.publishEvent(testEvent);

        var recentEvents = activityFeedService.getRecentEvents(10);
        assert recentEvents.size() == 1;
        assert recentEvents.get(0).getId().equals(testEvent.getId());
    }

    @Test
    @DisplayName("Should maintain events in order")
    @Description("Verifies that events are stored with most recent first")
    void shouldMaintainEventsInOrder() {
        ActivityEvent event1 = createTestEvent("INVOICE_CREATED");
        ActivityEvent event2 = createTestEvent("INVOICE_UPDATED");
        ActivityEvent event3 = createTestEvent("INVOICE_DELETED");

        activityFeedService.publishEvent(event1);
        activityFeedService.publishEvent(event2);
        activityFeedService.publishEvent(event3);

        var recentEvents = activityFeedService.getRecentEvents(10);
        assert recentEvents.size() == 3;
        assert recentEvents.get(0).getId().equals(event3.getId());
        assert recentEvents.get(1).getId().equals(event2.getId());
        assert recentEvents.get(2).getId().equals(event1.getId());
    }

    @Test
    @DisplayName("Should limit stored events to max size")
    @Description("Verifies that the event store doesn't exceed the maximum size")
    void shouldLimitStoredEventsToMaxSize() {
        // Publish more than MAX_EVENTS (100)
        for (int i = 0; i < 110; i++) {
            activityFeedService.publishEvent(createTestEvent("INVOICE_CREATED"));
        }

        var recentEvents = activityFeedService.getRecentEvents(200);
        assert recentEvents.size() == 100;
    }
}
