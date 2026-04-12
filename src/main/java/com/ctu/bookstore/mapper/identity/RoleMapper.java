package com.ctu.bookstore.mapper.identity;

import com.ctu.bookstore.dto.request.identity.RoleRequestDTO;
import com.ctu.bookstore.dto.respone.identity.RoleResponeDTO;
import com.ctu.bookstore.entity.identity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequestDTO roleRequestDTO);

    RoleResponeDTO toRoleRespone(Role role);
}
