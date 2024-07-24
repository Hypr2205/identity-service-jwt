package io.hypr.identityservice.config;

import io.hypr.identityservice.dto.request.IntrospectRequest;
import io.hypr.identityservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtDecoderConfig implements JwtDecoder {
    private final AuthenticationService authenticationService;
    private NimbusJwtDecoder nimbusJwtDecoder = null;
    @Value("${jwt.signer-key}")
    private String secretKey;

    @Override
    public Jwt decode(String token) throws JwtException {
        var response = authenticationService.introspect(IntrospectRequest
                                                            .builder()
                                                            .token(token)
                                                            .build());

        if (!response.isValid()) {
            throw new JwtException("Invalid token");
        }

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec spec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA512");
            nimbusJwtDecoder = NimbusJwtDecoder
                .withSecretKey(spec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
        }
        return nimbusJwtDecoder.decode(token);
    }
}
