package com.ctu.bookstore.dto.response.display;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponseDTO {
    String id;
    ProductResponseDTO product; // Dùng lại ProductResponse
    int quatity;
    Double subtotal; // priceAtTime * quantity
}
