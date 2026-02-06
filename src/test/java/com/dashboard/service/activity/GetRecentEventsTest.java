package com.dashboard.service.activity;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Story("Get Recent Events")
class GetRecentEventsTest extends BaseActivityFeedServiceTest {

    @Test
    @DisplayName("Should return empty list when no events")
    @Description("Verifies that getRecentEvents returns empty list when no events have been published")
    void shouldReturnEmptyListWhenNoEvents() {
        var recentEvents = activityFeedService.getRecentEvents(10);
        assertTrue(recentEvents.isEmpty());
    }

    @Test
    @DisplayName("Should return all events when limit exceeds count")
    @Description("Verifies that getRecentEvents returns all events when limit is higher than event count")
    void shouldReturnAllEventsWhenLimitExceedsCount() {
        activityFeedService.publishEvent(createTestEvent("INVOICE_CREATED"));
        activityFeedService.publishEvent(createTestEvent("INVOICE_UPDATED"));

        var recentEvents = activityFeedService.getRecentEvents(100);
        assertEquals(2, recentEvents.size());
    }

    @Test
    @DisplayName("Should limit events to requested count")
    @Description("Verifies that getRecentEvents respects the limit parameter")
    void shouldLimitEventsToRequestedCount() {
        for (int i = 0; i < 10; i++) {
            activityFeedService.publishEvent(createTestEvent("INVOICE_CREATED"));
        }

        var recentEvents = activityFeedService.getRecentEvents(5);
        assertEquals(5, recentEvents.size());
    }

    @Test
    @DisplayName("Should return most recent events first")
    @Description("Verifies that getRecentEvents returns events in reverse chronological order")
    void shouldReturnMostRecentEventsFirst() {
        var event1 = createTestEvent("INVOICE_CREATED");
        var event2 = createTestEvent("INVOICE_UPDATED");
        var event3 = createTestEvent("INVOICE_DELETED");

        activityFeedService.publishEvent(event1);
        activityFeedService.publishEvent(event2);
        activityFeedService.publishEvent(event3);

        var recentEvents = activityFeedService.getRecentEvents(2);
        assertEquals(2, recentEvents.size());
        assertEquals(event3.getId(), recentEvents.get(0).getId());
        assertEquals(event2.getId(), recentEvents.get(1).getId());
    }
}
