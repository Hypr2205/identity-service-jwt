package io.hypr.identityservice.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import io.hypr.identityservice.dto.request.AuthenticationRequest;
import io.hypr.identityservice.dto.response.AuthenticationResponse;
import io.hypr.identityservice.exception.AppException;
import io.hypr.identityservice.exception.ErrorCode;
import io.hypr.identityservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.signer-key}")
    private String secretKey;

    public AuthenticationResponse authenticated(AuthenticationRequest request) {
        var user = userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(request.getUsername());
        return AuthenticationResponse
            .builder()
            .token(token)
            .isAuthenticated(true)
            .build();
    }

    private String generateToken(String username) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
            .subject(username)
            .issuer("hypr.com")
            .issueTime(new Date())
            .expirationTime(new Date(Instant
                .now()
                .plus(1, ChronoUnit.HOURS)
                .toEpochMilli()))
            .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(secretKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Can't generate token", e);
            throw new RuntimeException(e);
        }
    }
}
