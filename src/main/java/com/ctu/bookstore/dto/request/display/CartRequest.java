package com.ctu.bookstore.dto.request.display;

import com.ctu.bookstore.entity.display.CartItem;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartRequest {
    String userID;
    Set<CartItem> cartItems;
}
