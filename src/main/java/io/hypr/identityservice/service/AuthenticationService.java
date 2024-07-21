package io.hypr.identityservice.service;

import io.hypr.identityservice.dto.request.AuthenticationRequest;
import io.hypr.identityservice.exception.AppException;
import io.hypr.identityservice.exception.ErrorCode;
import io.hypr.identityservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean authenticated(AuthenticationRequest request) {
        var user = userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return passwordEncoder.matches(request.getPassword(), user.getPassword());
    }
}
