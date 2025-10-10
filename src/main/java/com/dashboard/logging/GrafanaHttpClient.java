package com.dashboard.logging;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Service
public class GrafanaHttpClient {
    public void send() {
        String timestamp = String.valueOf(Instant.now().toEpochMilli() * 1000000);
        String log = "{" +
                "\"streams\":[" +
                "{" +
                "\"stream\":{\"language\":\"java\",\"source\":\"code\"}," +
                "\"values\":[[\"" + timestamp + "\",\"This is my log line\"]]" +
                "}" +
                "]" +
                "}";

        // Print this to verify the format
        System.out.println("Sending JSON: " + log);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://logs-prod-039.grafana.net/loki/api/v1/push"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer 1358926:glc_eyJvIjoiMTU1NjQ5OSIsIm4iOiJkYXNoYm9hcmQtc3ByaW50LWFwaSIsImsiOiJ0WDhmOGo0NURYRjhUQ1Q4NXM2eHo2TzUiLCJtIjp7InIiOiJwcm9kLWV1LWNlbnRyYWwtMCJ9fQ==")
                .POST(HttpRequest.BodyPublishers.ofString(log, StandardCharsets.UTF_8))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response code: " + response.statusCode());
            System.out.println("Response body: " + response.body());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
