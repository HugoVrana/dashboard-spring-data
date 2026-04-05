package com.dashboard.config;

import io.jsonwebtoken.Jwts;
import org.bson.types.ObjectId;

import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class TestJwtTokenGenerator {

    public static String generateValidToken(String username, List<String> grants) {
        Instant now = Instant.now();
        Instant expiration = now.plus(1, ChronoUnit.HOURS);

        return Jwts.builder()
                .subject(new ObjectId().toHexString())
                .claim("email", username)
                .claim("grants", grants)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(TestConfig.TEST_PRIVATE_KEY, Jwts.SIG.RS256)
                .compact();
    }

    public static String generateExpiredToken() {
        Instant past = Instant.now().minus(2, ChronoUnit.HOURS);
        Instant expiration = Instant.now().minus(1, ChronoUnit.HOURS);

        return Jwts.builder()
                .subject(new ObjectId().toHexString())
                .claim("email", "expiredUser")
                .claim("grants", List.of("dashboard-invoices-read"))
                .issuedAt(Date.from(past))
                .expiration(Date.from(expiration))
                .signWith(TestConfig.TEST_PRIVATE_KEY, Jwts.SIG.RS256)
                .compact();
    }

    public static String generateTokenWithInvalidSignature() {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            RSAPrivateKey otherKey = (RSAPrivateKey) gen.generateKeyPair().getPrivate();

            Instant now = Instant.now();
            return Jwts.builder()
                    .subject(new ObjectId().toHexString())
                    .claim("email", "testUser")
                    .claim("grants", List.of("dashboard-invoices-read"))
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                    .signWith(otherKey, Jwts.SIG.RS256)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate token with invalid signature", e);
        }
    }

    public static String generateValidToken(String... grants) {
        return generateValidToken("testUser", List.of(grants));
    }
}
