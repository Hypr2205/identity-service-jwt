package io.hypr.identityservice.service;

import io.hypr.identityservice.dto.request.RoleRequest;
import io.hypr.identityservice.dto.response.RoleResponse;
import io.hypr.identityservice.entity.Role;
import io.hypr.identityservice.mapper.RoleMapper;
import io.hypr.identityservice.repository.PermissionRepository;
import io.hypr.identityservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleMapper roleMapper;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public RoleResponse create(RoleRequest roleRequest) {
        Role role = roleMapper.toRole(roleRequest);

        var permissions = permissionRepository.findAllById(roleRequest.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll() {
        return roleRepository
            .findAll()
            .stream()
            .map(roleMapper::toRoleResponse)
            .toList();
    }

    public void delete(String role) {
        roleRepository.deleteById(role);
    }
}
