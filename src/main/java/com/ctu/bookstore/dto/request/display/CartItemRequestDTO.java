package com.ctu.bookstore.dto.request.display;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemRequestDTO {
    String productId;
    int quantity;
}
