package com.temple.auth.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Service
public class JwtService {

    private final byte[] secret;
    private final String issuer;
    private final long expMinutes;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.expMinutes}") long expMinutes
    ) {
        // HS256 requires at least 256-bit key => 32+ bytes
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.issuer = issuer;
        this.expMinutes = expMinutes;
    }

    public String generateToken(String email, String role) {
        try {
            Instant now = Instant.now();
            Instant exp = now.plusSeconds(expMinutes * 60);

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .issuer(issuer)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(exp))
                    .subject(email)
                    .claim("role", role)
                    .build();

            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            jwt.sign(new MACSigner(secret));
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to sign JWT", e);
        }
    }

    public DecodedToken verifyAndDecode(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);

            boolean valid = jwt.verify(new MACVerifier(secret));
            if (!valid) throw new IllegalArgumentException("Invalid JWT signature");

            JWTClaimsSet claims = jwt.getJWTClaimsSet();

            if (claims.getExpirationTime() == null || claims.getExpirationTime().before(new Date())) {
                throw new IllegalArgumentException("JWT expired");
            }

            if (claims.getIssuer() == null || !issuer.equals(claims.getIssuer())) {
                throw new IllegalArgumentException("Invalid issuer");
            }

            String email = claims.getSubject();
            String role = claims.getStringClaim("role");

            if (email == null || email.isBlank()) throw new IllegalArgumentException("Missing subject");
            if (role == null || role.isBlank()) role = "USER";

            return new DecodedToken(email, role);

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT", e);
        }
    }

    public record DecodedToken(String email, String role) {}
}
