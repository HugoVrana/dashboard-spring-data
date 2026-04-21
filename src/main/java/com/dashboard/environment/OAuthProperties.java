package com.dashboard.environment;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "oauth")
public final class OAuthProperties {

    private String clientId;

    private List<String> corsFallbackOrigins = List.of("http://localhost:3000");
}
