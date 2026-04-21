package com.dashboard.config;

import com.dashboard.environment.OAuthProperties;
import com.dashboard.filter.JwtGrantsFilter;
import com.dashboard.repository.IOAuthClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtGrantsFilter jwtGrantsFilter;
    private final OAuthProperties oAuthProperties;
    private final IOAuthClientRepository oAuthClientRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtGrantsFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws/**", "/ws-sockjs/**", "/api/activity/test").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        List<String> allowedOrigins = fetchAllowedHosts();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private List<String> fetchAllowedHosts() {
        if (!StringUtils.hasText(oAuthProperties.getClientId()) || !ObjectId.isValid(oAuthProperties.getClientId())) {
            List<String> fallbackOrigins = getFallbackOrigins();
            log.warn("No valid OAuth client ID configured, using fallback CORS origins: {}", fallbackOrigins);
            return fallbackOrigins;
        }
        return oAuthClientRepository
                .findBy_idAndAudit_DeletedAtIsNull(new ObjectId(oAuthProperties.getClientId()))
                .map(client -> {
                    List<String> allowedHosts = normalizeOrigins(client.getAllowedHosts());
                    log.info("Loaded {} CORS origin(s) from OAuth client", allowedHosts.size());
                    return allowedHosts;
                })
                .orElseGet(() -> {
                    List<String> fallbackOrigins = getFallbackOrigins();
                    log.warn("OAuth client not found, using fallback CORS origins: {}", fallbackOrigins);
                    return fallbackOrigins;
                });
    }

    private List<String> getFallbackOrigins() {
        return normalizeOrigins(oAuthProperties.getCorsFallbackOrigins());
    }

    private List<String> normalizeOrigins(List<String> origins) {
        if (origins == null) {
            return Collections.emptyList();
        }
        return origins.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();
    }
}
