package com.ctu.bookstore.mapper.identity;

import com.ctu.bookstore.dto.request.identity.RoleRequest;
import com.ctu.bookstore.dto.respone.identity.RoleRespone;
import com.ctu.bookstore.entity.identity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest roleRequest);

    RoleRespone toRoleRespone(Role role);
}
