package com.ctu.bookstore.dto.respone.display;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    String id;
    String userId;
    Set<CartItemResponse> cartItems;
    Double totalAmount;
}
