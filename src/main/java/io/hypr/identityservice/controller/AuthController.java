package io.hypr.identityservice.controller;

import io.hypr.identityservice.dto.request.AuthenticationRequest;
import io.hypr.identityservice.dto.request.IntrospectRequest;
import io.hypr.identityservice.dto.request.LogoutRequest;
import io.hypr.identityservice.dto.request.RefreshRequest;
import io.hypr.identityservice.dto.response.ApiResponse;
import io.hypr.identityservice.dto.response.AuthenticationResponse;
import io.hypr.identityservice.dto.response.IntrospectResponse;
import io.hypr.identityservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticated(request);
        return ApiResponse
            .<AuthenticationResponse>builder()
            .result(result)
            .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {
        var result = authenticationService.introspect(request);
        return ApiResponse
            .<IntrospectResponse>builder()
            .result(result)
            .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException {
        authenticationService.logout(request);
        return ApiResponse
            .<Void>builder()
            .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshRequest request) throws ParseException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse
            .<AuthenticationResponse>builder()
            .result(result)
            .build();
    }
}
