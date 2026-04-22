package com.ctu.bookstore.mapper.identity;

import com.ctu.bookstore.dto.request.identity.UserRequestDTO;
import com.ctu.bookstore.dto.request.identity.UserUpdateRequestDTO;
import com.ctu.bookstore.dto.response.identity.UserResponseDTO;
import com.ctu.bookstore.entity.identity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserRequestDTO userRequestDTO);

    UserResponseDTO toUserRespone(User user);

    // Do roles trong Request là String nhưng trong User là Role
    // do đó cần map thủ công chứ không dùng mapper để tự động đc
    @Mapping(target = "roles" , ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequestDTO userUpdateRequestDTO);

}
