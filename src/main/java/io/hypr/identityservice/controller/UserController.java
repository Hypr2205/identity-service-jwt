package io.hypr.identityservice.controller;

import io.hypr.identityservice.dto.request.CreateUserRequest;
import io.hypr.identityservice.dto.request.UpdateUserRequest;
import io.hypr.identityservice.dto.response.ApiResponse;
import io.hypr.identityservice.dto.response.UserResponse;
import io.hypr.identityservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        var authentication = SecurityContextHolder
            .getContext()
            .getAuthentication();

        log.info("Username: {}", authentication.getName());
        authentication
            .getAuthorities()
            .forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiResponse
            .<List<UserResponse>>builder()
            .result(userService.getAllUsers())
            .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable String id) {
        return ApiResponse
            .<UserResponse>builder()
            .result(userService.getUserById(id))
            .build();
    }

    @PostMapping
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ApiResponse
            .<UserResponse>builder()
            .code(201)
            .message("User created successfully!")
            .result(userService.createUserRequest(request))
            .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable String id, @RequestBody UpdateUserRequest request) {
        return ApiResponse
            .<UserResponse>builder()
            .result(userService.updateUser(id, request))
            .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ApiResponse
            .<String>builder()
            .result("User has been deleted successfully!")
            .build();
    }
}
