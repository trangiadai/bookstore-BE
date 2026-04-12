package com.ctu.bookstore.dto.request.payment;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckoutItemRequestDTO {
    String productId;
    int quantity;
    String paymentMethod;
}
