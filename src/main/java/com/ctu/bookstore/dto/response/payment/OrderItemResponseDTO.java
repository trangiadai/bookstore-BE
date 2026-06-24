package com.ctu.bookstore.dto.response.payment;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponseDTO {
    Long id;
    Long productId;
    String productName;
    int quantity;
    double priceAtTime;
}