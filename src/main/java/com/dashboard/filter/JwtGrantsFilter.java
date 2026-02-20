package com.dashboard.filter;

import com.dashboard.authentication.GrantsAuthentication;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

@Component
@Order(1)
public class JwtGrantsFilter extends OncePerRequestFilter {

    @Value("${JWT.SECRET}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);

            if (token != null) {
                Claims claims = validateAndParse(token);
                List<String> grants = claims.get("grants", List.class);
                String userId = claims.get("userId", String.class);
                String profileImageUrl = claims.get("profileImageUrl", String.class);

                // Set authentication in SecurityContext
                SecurityContextHolder.getContext()
                        .setAuthentication(new GrantsAuthentication(claims.getSubject(), userId, profileImageUrl, grants));
            }
        } catch (Exception e) {
            // Token invalid or expired - continue without auth
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

    private Claims validateAndParse(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token");
        }
    }
}