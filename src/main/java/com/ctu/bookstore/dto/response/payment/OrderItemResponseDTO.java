package com.ctu.bookstore.dto.response.payment;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponseDTO {
    Long id;
    Long productId;
    String productName;
    int quantity;
    double priceAtTime;
}