package io.hypr.identityservice.controller;

import io.hypr.identityservice.dto.request.RoleRequest;
import io.hypr.identityservice.dto.response.ApiResponse;
import io.hypr.identityservice.dto.response.RoleResponse;
import io.hypr.identityservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    public ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest roleRequest) {
        return ApiResponse
            .<RoleResponse>builder()
            .result(roleService.create(roleRequest))
            .build();
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> getRoles() {
        return ApiResponse
            .<List<RoleResponse>>builder()
            .result(roleService.getAll())
            .build();
    }

    @DeleteMapping("/{role}")
    public ApiResponse<Void> deleteRole(@PathVariable("role") String role) {
        roleService.delete(role);
        return ApiResponse
            .<Void>builder()
            .build();
    }
}
