package com.ctu.bookstore.dto.respone.display;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponseDTO {
    String id;
    String userId;
    Set<CartItemResponseDTO> cartItems;
    Double totalAmount;
}
