package com.dashboard.filter;

import com.dashboard.authentication.GrantsAuthentication;
import com.dashboard.common.logging.GrafanaHttpClient;
import com.dashboard.common.model.log.ApiCallLog;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class JwtGrantsFilter extends OncePerRequestFilter {

    private static final String SERVICE_NAME = "spring-dashboard";

    private final JwtDecoder jwtDecoder;
    private final GrafanaHttpClient grafanaHttpClient;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);

            if (token != null) {
                Jwt jwt = jwtDecoder.decode(token);
                List<String> grants = jwt.getClaimAsStringList("grants");
                String userId = jwt.getSubject();
                String email = jwt.getClaimAsString("email");

                SecurityContextHolder.getContext()
                        .setAuthentication(new GrantsAuthentication(email, userId, "", grants));
            }
        } catch (Exception e) {
            // Token invalid or expired - continue without auth
            try {
                ApiCallLog callLog = ApiCallLog.builder()
                        .service(SERVICE_NAME)
                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                        .statusMessage(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                        .timestamp(Instant.now())
                        .method(request.getMethod())
                        .requestId(request.getRequestId())
                        .level("warn")
                        .environment(request.getServletContext().getContextPath())
                        .clientIp(request.getRemoteAddr())
                        .endpoint(request.getRequestURI())
                        .userAgent(request.getHeader("User-Agent"))
                        .errorType(e.getClass().getSimpleName())
                        .errorMessage(e.getMessage())
                        .build();
                grafanaHttpClient.send(callLog);
            } catch (Exception loggingException) {
                log.warn("Failed to log JWT validation failure", loggingException);
            }
            log.warn("JWT validation failed [{}] path={} reason={}", e.getClass().getSimpleName(), request.getRequestURI(), e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return null;
        }
        String token = bearerToken.substring(7).trim();
        return token.isEmpty() ? null : token;
    }
}
