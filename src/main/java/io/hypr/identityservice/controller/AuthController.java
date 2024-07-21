package io.hypr.identityservice.controller;

import io.hypr.identityservice.dto.request.AuthenticationRequest;
import io.hypr.identityservice.dto.response.ApiResponse;
import io.hypr.identityservice.dto.response.AuthenticationResponse;
import io.hypr.identityservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
