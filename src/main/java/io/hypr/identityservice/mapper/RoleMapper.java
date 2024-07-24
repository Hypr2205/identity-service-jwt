package io.hypr.identityservice.mapper;

import io.hypr.identityservice.dto.request.RoleRequest;
import io.hypr.identityservice.dto.response.RoleResponse;
import io.hypr.identityservice.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
