package com.dashboard.interceptor;

import com.dashboard.logging.GrafanaHttpClient;
import com.dashboard.logging.LogBuilderHelper;
import com.dashboard.model.log.ApiCallLog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        // Store start time and request ID for duration calculation
        request.setAttribute(REQUEST_START_TIME, Instant.now());
        request.setAttribute(REQUEST_ID, UUID.randomUUID().toString());
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                @NotNull Object handler, Exception ex) {
        // Only log if NO exception occurred (exceptions are handled by GlobalExceptionHandler)
        if (ex == null) {
            try {
                ApiCallLog log = captureApiCall(request, response);
                grafanaHttpClient.send(log);
            } catch (Exception e) {
                log.error("Failed to log API call", e);
            }
        }
    }

    private ApiCallLog captureApiCall(HttpServletRequest request, HttpServletResponse response) {
        Instant startTime = (Instant) request.getAttribute(REQUEST_START_TIME);
        Instant endTime = Instant.now();
        Long durationMs = startTime != null ?
                java.time.Duration.between(startTime, endTime).toMillis() : null;

        Instant timestamp = startTime != null ? startTime : Instant.now();
        ApiCallLog.ApiCallLogBuilder builder = LogBuilderHelper.buildBaseLog(
                request,
                response.getStatus(),
                timestamp,
                durationMs
        );

        builder.userId(extractUserId(request))
                .headers(extractHeaders(request));

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

    private String extractUserId(HttpServletRequest request) {
        if (request.getUserPrincipal() != null) {
            return request.getUserPrincipal().getName(); // this is just to we make warnings shut up
        }
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
                String characterEncoding = request.getCharacterEncoding();
                String requestBodyAsString = new String(content, characterEncoding);
                return objectMapper.readValue(requestBodyAsString, new TypeReference<>() {});
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
                return objectMapper.readValue(body, new TypeReference<>(){});
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
}