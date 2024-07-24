package io.hypr.identityservice.mapper;

import io.hypr.identityservice.dto.request.PermissionRequest;
import io.hypr.identityservice.dto.response.PermissionResponse;
import io.hypr.identityservice.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
