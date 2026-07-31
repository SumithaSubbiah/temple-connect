package com.temple.gateway.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;

@Component
public class JwtVerifier {

    private final byte[] secret;
    private final String issuer;

    public JwtVerifier(@Value("${app.jwt.secret}") String secret,
                       @Value("${app.jwt.issuer}") String issuer) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.issuer = issuer;
    }

    public DecodedToken verify(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);

            if (!JWSAlgorithm.HS256.equals(jwt.getHeader().getAlgorithm())) {
                throw new IllegalArgumentException("Unsupported JWT algorithm");
            }

            boolean ok = jwt.verify(new MACVerifier(secret));
            if (!ok) throw new IllegalArgumentException("Invalid JWT signature");

            var claims = jwt.getJWTClaimsSet();

            if (claims.getIssuer() == null || !issuer.equals(claims.getIssuer())) {
                throw new IllegalArgumentException("Invalid issuer");
            }

            Date exp = claims.getExpirationTime();
            if (exp == null || exp.toInstant().isBefore(Instant.now())) {
                throw new IllegalArgumentException("Expired token");
            }

            String email = claims.getSubject();
            String role = claims.getStringClaim("role");
            if (email == null || email.isBlank()) throw new IllegalArgumentException("Missing subject");
            if (role == null || role.isBlank()) role = "USER";

            return new DecodedToken(email, role);

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token", e);
        }
    }

    public record DecodedToken(String email, String role) {}
}
