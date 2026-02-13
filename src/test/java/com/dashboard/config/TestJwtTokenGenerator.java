package com.dashboard.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * Utility class for generating JWT tokens in tests.
 * Uses the same secret as the application for valid tokens.
 */
public class TestJwtTokenGenerator {

    // Same secret as in application.properties for valid token generation
    private static final String JWT_SECRET = "pHop9CyAIzw/Y6OHA3DXkZAFJxWRht2d2ROClZQNbSw=";

    // Different secret for invalid signature testing
    private static final String INVALID_SECRET = "aW52YWxpZFNlY3JldEtleUZvclRlc3RpbmdQdXJwb3Nlcw==";

    /**
     * Generates a valid JWT token with the specified username and grants.
     *
     * @param username the username to set as subject
     * @param grants the list of grants/permissions
     * @return a valid JWT token string
     */
    public static String generateValidToken(String username, List<String> grants) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));

        Instant now = Instant.now();
        Instant expiration = now.plus(1, ChronoUnit.HOURS);

        return Jwts.builder()
                .subject(username)
                .claim("grants", grants)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(key)
                .compact();
    }

    /**
     * Generates an expired JWT token.
     *
     * @return an expired JWT token string
     */
    public static String generateExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));

        Instant past = Instant.now().minus(2, ChronoUnit.HOURS);
        Instant expiration = Instant.now().minus(1, ChronoUnit.HOURS);

        return Jwts.builder()
                .subject("expiredUser")
                .claim("grants", List.of("dashboard-invoices-read"))
                .issuedAt(Date.from(past))
                .expiration(Date.from(expiration))
                .signWith(key)
                .compact();
    }

    /**
     * Generates a JWT token with an invalid signature.
     *
     * @return a JWT token string with invalid signature
     */
    public static String generateTokenWithInvalidSignature() {
        SecretKey invalidKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(INVALID_SECRET));

        Instant now = Instant.now();
        Instant expiration = now.plus(1, ChronoUnit.HOURS);

        return Jwts.builder()
                .subject("testUser")
                .claim("grants", List.of("dashboard-invoices-read"))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(invalidKey)
                .compact();
    }

    /**
     * Generates a valid JWT token with the specified grants for the default test user.
     *
     * @param grants the grants to include
     * @return a valid JWT token string
     */
    public static String generateValidToken(String... grants) {
        return generateValidToken("testUser", List.of(grants));
    }
}
