package com.ctu.bookstore.mapper.identity;

import com.ctu.bookstore.dto.request.identity.RoleRequestDTO;
import com.ctu.bookstore.dto.response.identity.RoleResposeDTO;
import com.ctu.bookstore.entity.identity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    // vì roles trong request là String mà trong Role là Permission đó đó cần bỏ qua nó
    // sau đó mình tự map vào sau
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequestDTO roleRequestDTO);

    RoleResposeDTO toRoleRespone(Role role);
}
