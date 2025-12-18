package com.ctu.bookstore.mapper.identity;

import com.ctu.bookstore.dto.request.identity.PermissionRequest;
import com.ctu.bookstore.dto.respone.identity.PermissionRespone;
import com.ctu.bookstore.entity.identity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest permissionRequest);
    PermissionRespone toPermissionRespone(Permission permission);
}
