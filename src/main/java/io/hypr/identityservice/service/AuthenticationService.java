package io.hypr.identityservice.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.hypr.identityservice.dto.request.AuthenticationRequest;
import io.hypr.identityservice.dto.request.IntrospectRequest;
import io.hypr.identityservice.dto.request.LogoutRequest;
import io.hypr.identityservice.dto.request.RefreshRequest;
import io.hypr.identityservice.dto.response.AuthenticationResponse;
import io.hypr.identityservice.dto.response.IntrospectResponse;
import io.hypr.identityservice.entity.InvalidatedToken;
import io.hypr.identityservice.entity.User;
import io.hypr.identityservice.exception.AppException;
import io.hypr.identityservice.exception.ErrorCode;
import io.hypr.identityservice.repository.InvalidateTokenRepository;
import io.hypr.identityservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InvalidateTokenRepository invalidateTokenRepository;

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

        var token = generateToken(user);

        return AuthenticationResponse
            .builder()
            .token(token)
            .isAuthenticated(true)
            .build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token);
        } catch (AppException e) {
            isValid = false;
        }
        return IntrospectResponse
            .builder()
            .valid(isValid)
            .build();
    }

    public void logout(LogoutRequest request) throws ParseException {
        var signedToken = verifyToken(request.getToken());
        String jid = signedToken
            .getJWTClaimsSet()
            .getJWTID();
        Date expiration = signedToken
            .getJWTClaimsSet()
            .getExpirationTime();
        var invalidatedToken = InvalidatedToken
            .builder()
            .id(jid)
            .expiryDate(expiration)
            .build();
        invalidateTokenRepository.save(invalidatedToken);
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException {
        var signedJWT = verifyToken(request.getToken());

        var jit = signedJWT
            .getJWTClaimsSet()
            .getJWTID();
        var expirationTime = signedJWT
            .getJWTClaimsSet()
            .getExpirationTime();

        var invalidatedToken = InvalidatedToken
            .builder()
            .id(jit)
            .expiryDate(expirationTime)
            .build();
        invalidateTokenRepository.save(invalidatedToken);

        var username = signedJWT
            .getJWTClaimsSet()
            .getSubject();

        var user = userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var token = generateToken(user);

        return AuthenticationResponse
            .builder()
            .token(token)
            .isAuthenticated(true)
            .build();
    }

    private SignedJWT verifyToken(String token) {
        try {
            JWSVerifier verifier = new MACVerifier(secretKey.getBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);
            Date expiration = signedJWT
                .getJWTClaimsSet()
                .getExpirationTime();
            var verified = signedJWT.verify(verifier);
            if (!(verified && expiration.after(new Date()))) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
            if (invalidateTokenRepository.existsById(signedJWT
                                                         .getJWTClaimsSet()
                                                         .getJWTID())) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
            return signedJWT;
        } catch (JOSEException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
            .subject(user.getUsername())
            .issueTime(new Date())
            .expirationTime(new Date(Instant
                                         .now()
                                         .plus(1, ChronoUnit.HOURS)
                                         .toEpochMilli()))
            .jwtID(UUID
                       .randomUUID()
                       .toString())
            .claim("scope", buildScope(user))
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

    private String buildScope(User user) {
        StringJoiner joiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user
                .getRoles()
                .forEach(role -> {
                    joiner.add("ROLE_" + role.getName());
                    if (!CollectionUtils.isEmpty(role.getPermissions())) {
                        role
                            .getPermissions()
                            .forEach(permission -> joiner.add(permission.getName()));
                    }
                });
        }
        return joiner.toString();
    }
}
