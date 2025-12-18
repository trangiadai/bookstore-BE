package com.ctu.bookstore.mapper.display;


import com.ctu.bookstore.dto.respone.display.CartResponse;
import com.ctu.bookstore.entity.display.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {
        @Mapping(source = "user.id", target = "userId") // Map trường user.id sang userId
        @Mapping(target = "totalAmount", expression = "java(mapTotalAmount(cart))") // Tính tổng tiền
        CartResponse toCartResponse(Cart cart);


        default Double mapTotalAmount(Cart cart) {
            if (cart.getCartItems() == null) return 0.0;
            System.out.println("Cart trong CartMapper"+cart);
            return cart.getCartItems().stream()
//                    .mapToDouble(item -> item.getProduct().getSellingPrice() * item.getQuantity())
                    .mapToDouble(item -> item.getProduct().getSellingPrice() * item.getQuatity())
                    .sum();
        }



}
