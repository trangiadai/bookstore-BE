package com.ctu.bookstore.mapper.identity;

import com.ctu.bookstore.dto.request.identity.UserRequestDTO;
import com.ctu.bookstore.dto.request.identity.UserUpdateRequestDTO;
import com.ctu.bookstore.dto.respone.identity.UserResponeDTO;
import com.ctu.bookstore.entity.identity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserRequestDTO userRequestDTO);

    UserResponeDTO toUserRespone(User user);
    @Mapping(target = "roles" , ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequestDTO userUpdateRequestDTO);

}
