package com.ctu.bookstore.mapper.identity;

import com.ctu.bookstore.dto.request.identity.UserRequest;
import com.ctu.bookstore.dto.request.identity.UserUpdateRequest;
import com.ctu.bookstore.dto.respone.identity.UserRespone;
import com.ctu.bookstore.entity.identity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserRequest userRequest);

    UserRespone toUserRespone(User user);
    @Mapping(target = "roles" , ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest userUpdateRequest);

}
