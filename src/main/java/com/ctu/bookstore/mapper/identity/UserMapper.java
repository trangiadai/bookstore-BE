package com.ctu.bookstore.mapper.identity;

import com.ctu.bookstore.dto.request.identity.UserRequestDTO;
import com.ctu.bookstore.dto.response.identity.UserResponseDTO;
import com.ctu.bookstore.entity.identity.Permission;
import com.ctu.bookstore.entity.identity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserRequestDTO userRequestDTO);

    UserResponseDTO toUserRespone(User user);

    // Bypassing automated mapping for roles during update updates
    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserRequestDTO userRequestDTO);

    /**
     * MapStruct will automatically detect this method and use it to map
     * Set<String> permissions to Set<Permission> permissions inside Roles mapping.
     */
    default Set<Permission> mapPermissions(Set<String> permissions) {
        if (permissions == null) {
            return null;
        }
        return permissions.stream()
                .map(permissionName -> Permission.builder()
                        .name(permissionName)
                        .build())
                .collect(Collectors.toSet());
    }
}