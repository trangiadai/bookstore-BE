package com.ctu.bookstore.mapper.display;

import com.ctu.bookstore.dto.respone.display.CartItemResponse;
import com.ctu.bookstore.entity.display.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CartItemMapper {
    @Mapping(target = "subtotal", expression = "java(item.getProduct().getSellingPrice() * item.getQuatity())")
    CartItemResponse toResponse(CartItem item);
}
