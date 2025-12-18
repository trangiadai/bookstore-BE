package com.ctu.bookstore.mapper.payment;

import com.ctu.bookstore.dto.respone.payment.OrderItemResponse;
import com.ctu.bookstore.entity.payment.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}
