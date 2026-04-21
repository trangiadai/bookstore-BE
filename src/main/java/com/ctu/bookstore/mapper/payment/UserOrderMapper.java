package com.ctu.bookstore.mapper.payment;

import com.ctu.bookstore.dto.response.payment.UserOrderResponseDTO;
import com.ctu.bookstore.entity.payment.UserOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = { OrderItemMapper.class })
public interface UserOrderMapper {
    @Mapping(source = "user.id", target = "userId")
    UserOrderResponseDTO toUserOrderResponse(UserOrder userOrder);
}
