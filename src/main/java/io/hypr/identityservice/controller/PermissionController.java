package io.hypr.identityservice.controller;

import io.hypr.identityservice.dto.request.PermissionRequest;
import io.hypr.identityservice.dto.response.ApiResponse;
import io.hypr.identityservice.dto.response.PermissionResponse;
import io.hypr.identityservice.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping
    public ApiResponse<PermissionResponse> createRole(@RequestBody PermissionRequest request) {
        return ApiResponse
            .<PermissionResponse>builder()
            .result(permissionService.create(request))
            .build();
    }

    @GetMapping
    public ApiResponse<List<PermissionResponse>> getRoles() {
        return ApiResponse
            .<List<PermissionResponse>>builder()
            .result(permissionService.getAll())
            .build();
    }

    @DeleteMapping("/{permission}")
    public ApiResponse<Void> deleteRole(@PathVariable String permission) {
        permissionService.delete(permission);
        return ApiResponse
            .<Void>builder()
            .build();
    }
}
