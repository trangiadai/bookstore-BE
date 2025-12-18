package com.ctu.bookstore.dto.respone.display;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    String id;
    ProductResponse product; // Dùng lại ProductResponse
    int quatity;
    Double subtotal; // priceAtTime * quantity
}
