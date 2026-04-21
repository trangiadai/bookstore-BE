package com.ctu.bookstore.mapper.payment;

import com.ctu.bookstore.dto.response.payment.OrderItemResponseDTO;
import com.ctu.bookstore.entity.payment.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemResponseDTO toOrderItemResponse(OrderItem orderItem);
}
