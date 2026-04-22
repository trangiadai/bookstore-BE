package com.ctu.bookstore.mapper.identity;

import com.ctu.bookstore.dto.request.identity.PermissionRequestDTO;
import com.ctu.bookstore.dto.response.identity.PermissionResponeDTO;
import com.ctu.bookstore.entity.identity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequestDTO permissionRequestDTO);

    PermissionResponeDTO toPermissionRespone(Permission permission);
}
