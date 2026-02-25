package com.dashboard.controller;

import com.dashboard.common.model.ActivityEvent;
import com.dashboard.service.interfaces.IActivityFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final IActivityFeedService activityFeedService;

    @GetMapping("/recent")
    public List<ActivityEvent> getRecentEvents(
            @RequestParam(defaultValue = "50") int limit) {
        return activityFeedService.getRecentEvents(limit);
    }
}