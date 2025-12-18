package com.ctu.bookstore.dto.respone.payment;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {
    Long id;
    Long productId;
    String productName;
    int quantity;
    double priceAtTime;
}