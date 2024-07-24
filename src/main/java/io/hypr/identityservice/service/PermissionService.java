package io.hypr.identityservice.service;

import io.hypr.identityservice.dto.request.PermissionRequest;
import io.hypr.identityservice.dto.response.PermissionResponse;
import io.hypr.identityservice.entity.Permission;
import io.hypr.identityservice.mapper.PermissionMapper;
import io.hypr.identityservice.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionMapper permissionMapper;
    private final PermissionRepository permissionRepository;

    public PermissionResponse create(PermissionRequest permissionRequest) {
        Permission permission = permissionMapper.toPermission(permissionRequest);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll() {
        List<Permission> permissions = permissionRepository.findAll();
        return permissions
            .stream()
            .map(permissionMapper::toPermissionResponse)
            .toList();
    }

    public void delete(String permission) {
        permissionRepository.deleteById(permission);
    }
}
