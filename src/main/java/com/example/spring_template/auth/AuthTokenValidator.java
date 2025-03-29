package com.example.spring_template.auth;

import com.example.spring_template.domain.enums.LogLevel;
import com.example.spring_template.domain.enums.Roles;
import com.example.spring_template.service.log.LogService;
import com.example.spring_template.util.AuthContextUtil;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class AuthTokenValidator {
    @Value("${betterauth.jwks.url}")
    private String jwksUrlString;

    @Value("${betterauth.issuer}")
    private String expectedIssuer;

    @Value("${betterauth.audience}")
    private String expectedAudience;

    private final LogService logService;

    @Autowired
    public AuthTokenValidator(LogService logService) {
        this.logService = logService;
    }

    //This token validation is specially designed for better-auth
    public User validateTokenAndExtract(String token) {
        try {
            URL jwksUrl = new URI(jwksUrlString).toURL();

            // Retrieve and parse JWKS
            JWKSet jwkSet = JWKSet.load(jwksUrl);

            // Parse JWT to extract 'kid' from header
            SignedJWT signedJWT = SignedJWT.parse(token);
            String kid = signedJWT.getHeader().getKeyID();

            if (kid == null) {
                logService.log(LogLevel.WARN)
                        .setSource(this.getClass().getSimpleName())
                        .setMessage("Missing kid in JWT header")
                        .build();
                return null;
            }

            // Find the key with the expected 'kid'
            JWK matchingJWK = jwkSet.getKeys().stream()
                    .filter(k -> kid.equals(k.getKeyID()) && k instanceof OctetKeyPair)
                    .findFirst()
                    .orElse(null);

            if (!(matchingJWK instanceof OctetKeyPair okp)) {
                logService.log(LogLevel.WARN)
                        .setSource(this.getClass().getSimpleName())
                        .setMessage("No Ed25519 key found for kid: " + kid)
                        .build();
                return null;
            }

            // Cast to OctetKeyPair and get verifier
            JWSVerifier verifier = new Ed25519Verifier(okp);

            // Parse the JWT and verify
            if (!signedJWT.verify(verifier)) {
                logService.log(LogLevel.WARN)
                        .setSource(this.getClass().getSimpleName())
                        .setMessage("JWT signature verification failed")
                        .build();
                return null;
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            String userId = claimsSet.getSubject();

            // Validate claims
            String issuer = claimsSet.getIssuer();
            List<String> audience = claimsSet.getAudience();
            if (!expectedIssuer.equals(issuer) || audience == null || !audience.contains(expectedAudience)) {
                logService.log(LogLevel.WARN)
                        .setSource(this.getClass().getSimpleName())
                        .setMessage("Invalid issuer or audience")
                        .setUser(userId)
                        .build();
                return null;
            }

            String email = claimsSet.getStringClaim("email");
            if (email == null || email.isBlank()) return null;

            boolean banned = false;
            try{
                banned = claimsSet.getBooleanClaim("banned");
            }catch (Exception ignored){
            }

            if (banned) {
                logService.log(LogLevel.WARN)
                        .setSource(this.getClass().getSimpleName())
                        .setMessage("User is banned")
                        .setDetails("email=" + email)
                        .setUser(userId)
                        .build();
                return null;
            }

            String roleString = claimsSet.getStringClaim("role");
            Roles role;
            try {
                role = (roleString != null) ? Roles.valueOf(roleString) : Roles.USER;
            } catch (IllegalArgumentException e) {
                // fallback or throw custom exception
                role = Roles.USER;
            }

            Date expirationTime = claimsSet.getExpirationTime();
            if (expirationTime == null || expirationTime.before(new Date())) {
                logService.log(LogLevel.WARN)
                        .setSource(this.getClass().getSimpleName())
                        .setMessage("JWT has expired")
                        .setDetails("email=" + email)
                        .setUser(userId)
                        .build();
                return null;
            }

            Date notBeforeTime = claimsSet.getNotBeforeTime();
            Date issuedAt = claimsSet.getIssueTime();
            Date now = new Date();

            if (notBeforeTime != null && now.before(notBeforeTime)) return null;
            if (issuedAt != null && now.before(issuedAt)) return null;

            // Add to MDC
            String jti = claimsSet.getJWTID();
            MDC.put("jwtId", jti);
            MDC.put("user", email);
            MDC.put("role", role.name());
            MDC.put("requestId", UUID.randomUUID().toString());
            MDC.put("ip", AuthContextUtil.getIpAddress());
            MDC.put("agent", AuthContextUtil.getUserAgent());

            logService.log(LogLevel.INFO)
                    .setSource(this.getClass().getSimpleName())
                    .setMessage("JWT authentication succeeded")
                    .setDetails("email=" + email + ", role=" + role)
                    .setUser(userId)
                    .build();

            return new User(email, "", List.of(new SimpleGrantedAuthority(role.name())));

        } catch (Exception e) {
            logService.log(LogLevel.ERROR)
                    .setSource(this.getClass().getSimpleName())
                    .setMessage("JWT validation failed")
                    .setDetails("[" + e.getClass().getSimpleName() + "] " + e.getMessage())
                    .build();
            return null;
        }
    }
}
