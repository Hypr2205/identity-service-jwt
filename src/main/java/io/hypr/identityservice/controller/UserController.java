package io.hypr.identityservice.controller;

import io.hypr.identityservice.dto.request.CreateUserRequest;
import io.hypr.identityservice.dto.request.UpdateUserRequest;
import io.hypr.identityservice.dto.response.ApiResponse;
import io.hypr.identityservice.entity.User;
import io.hypr.identityservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ApiResponse<List<User>> getAllUsers() {
        return ApiResponse
            .<List<User>>builder()
            .result(userService.getAllUsers())
            .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable String id) {
        return ApiResponse
            .<User>builder()
            .result(userService.getUserById(id))
            .build();
    }

    @PostMapping
    public ApiResponse<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ApiResponse
            .<User>builder()
            .code(201)
            .message("User created successfully!")
            .result(userService.createUserRequest(request))
            .build();
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable String id, @RequestBody UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }
}
