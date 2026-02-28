package com.dashboard.controller;

import com.dashboard.common.model.ActivityEvent;
import com.dashboard.service.interfaces.IActivityFeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Tag(name = "Activity", description = "Activity feed operations")
@RequiredArgsConstructor
@RequestMapping(value = "/api/activity", produces = "application/json")
public class ActivityController {

    private final IActivityFeedService activityFeedService;

    @Operation(summary = "Get recent activity events",
            description = "Retrieves the most recent activity events from the feed")
    @GetMapping("/recent")
    public List<ActivityEvent> getRecentEvents(
            @Parameter(description = "Maximum number of events to return") @RequestParam(defaultValue = "50") int limit) {
        return activityFeedService.getRecentEvents(limit);
    }
}