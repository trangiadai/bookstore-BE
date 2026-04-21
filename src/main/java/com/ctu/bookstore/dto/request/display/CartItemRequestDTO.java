package com.ctu.bookstore.dto.request.display;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemRequestDTO {
    String productId;
    @NotNull(message = "quantity của sản phẩm để thêm vào giỏ ít nhất là 1")
    int quantity;
}
