package com.dashboard.interceptor;

import com.dashboard.logging.GrafanaHttpClient;
import com.dashboard.model.log.ApiCallLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import java.time.Instant;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_START_TIME = "requestStartTime";
    private static final String REQUEST_ID = "requestId";

    private final GrafanaHttpClient grafanaHttpClient;
    private final ObjectMapper objectMapper;

    public ApiLoggingInterceptor(GrafanaHttpClient grafanaHttpClient, ObjectMapper objectMapper) {
        this.grafanaHttpClient = grafanaHttpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Store start time and request ID for duration calculation
        request.setAttribute(REQUEST_START_TIME, Instant.now());
        request.setAttribute(REQUEST_ID, UUID.randomUUID().toString());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        try {
            ApiCallLog log = captureApiCall(request, response, ex);
            grafanaHttpClient.send(log);
        } catch (Exception e) {
            log.error("Failed to log API call", e);
        }
    }

    private ApiCallLog captureApiCall(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        Instant startTime = (Instant) request.getAttribute(REQUEST_START_TIME);
        Instant endTime = Instant.now();
        Long durationMs = startTime != null ?
                java.time.Duration.between(startTime, endTime).toMillis() : null;

        ApiCallLog.ApiCallLogBuilder builder = ApiCallLog.builder()
                // Request Information
                .requestId((String) request.getAttribute(REQUEST_ID))
                .method(request.getMethod())
                .endpoint(request.getRequestURI())
                .fullUrl(getFullUrl(request))

                // Timing Information
                .timestamp(startTime != null ? startTime : Instant.now())
                .durationMs(durationMs)

                // Response Information
                .statusCode(response.getStatus())
                .statusMessage(getStatusMessage(response.getStatus()))

                // Client Information
                .clientIp(getClientIp(request))
                .userAgent(request.getHeader("User-Agent"))
                .userId(extractUserId(request))

                // Additional Context
                .headers(extractHeaders(request))
                .service("Dashboard API - Spring")
                .environment(System.getProperty("spring.profiles.active", "dev"))
                .version("1.0.0");

        // Handle errors
        if (ex != null) {
            builder.errorMessage(ex.getMessage())
                    .errorType(ex.getClass().getSimpleName())
                    .stackTrace(getStackTrace(ex));
        }

        // Capture request/response bodies if wrapped
        if (request instanceof ContentCachingRequestWrapper) {
            builder.requestBody(extractRequestBody((ContentCachingRequestWrapper) request))
                    .requestSize(getRequestSize((ContentCachingRequestWrapper) request));
        }

        if (response instanceof ContentCachingResponseWrapper) {
            builder.responseBody(extractResponseBody((ContentCachingResponseWrapper) response))
                    .responseSize(getResponseSize((ContentCachingResponseWrapper) response));
        }

        return builder.build();
    }

    private String getFullUrl(HttpServletRequest request) {
        StringBuilder url = new StringBuilder(request.getRequestURL());
        String queryString = request.getQueryString();
        if (queryString != null) {
            url.append("?").append(queryString);
        }
        return url.toString();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For can contain multiple IPs, take the first one
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String extractUserId(HttpServletRequest request) {
        // Example for JWT:
        // String authHeader = request.getHeader("Authorization");
        // if (authHeader != null && authHeader.startsWith("Bearer ")) {
        //     return extractUserIdFromJwt(authHeader.substring(7));
        // }
        return null;
    }

    private Map<String, String> extractHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            // Filter out sensitive headers
            if (!isSensitiveHeader(headerName)) {
                headers.put(headerName, request.getHeader(headerName));
            }
        }

        return headers;
    }

    private boolean isSensitiveHeader(String headerName) {
        String lower = headerName.toLowerCase();
        return lower.equals("authorization") ||
                lower.equals("cookie") ||
                lower.equals("set-cookie") ||
                lower.equals("x-api-key");
    }

    private Map<String, Object> extractRequestBody(ContentCachingRequestWrapper request) {
        try {
            byte[] content = request.getContentAsByteArray();
            if (content.length > 0) {
                String body = new String(content, request.getCharacterEncoding());
                return objectMapper.readValue(body, Map.class);
            }
        } catch (Exception e) {
            log.debug("Could not parse request body", e);
        }
        return null;
    }

    private Map<String, Object> extractResponseBody(ContentCachingResponseWrapper response) {
        try {
            byte[] content = response.getContentAsByteArray();
            if (content.length > 0) {
                String body = new String(content, response.getCharacterEncoding());
                return objectMapper.readValue(body, Map.class);
            }
        } catch (Exception e) {
            log.debug("Could not parse response body", e);
        }
        return null;
    }

    private Long getRequestSize(ContentCachingRequestWrapper request) {
        return (long) request.getContentAsByteArray().length;
    }

    private Long getResponseSize(ContentCachingResponseWrapper response) {
        return (long) response.getContentAsByteArray().length;
    }

    private String getStatusMessage(int statusCode) {
        return switch (statusCode / 100) {
            case 2 -> "Success";
            case 3 -> "Redirect";
            case 4 -> "Client Error";
            case 5 -> "Server Error";
            default -> "Unknown";
        };
    }

    private String getStackTrace(Exception ex) {
        // Limit stack trace size for logging
        StringBuilder sb = new StringBuilder();
        sb.append(ex.toString()).append("\n");

        StackTraceElement[] elements = ex.getStackTrace();
        int limit = Math.min(elements.length, 10); // Only first 10 lines

        for (int i = 0; i < limit; i++) {
            sb.append("\tat ").append(elements[i]).append("\n");
        }

        if (elements.length > limit) {
            sb.append("\t... ").append(elements.length - limit).append(" more");
        }

        return sb.toString();
    }
}