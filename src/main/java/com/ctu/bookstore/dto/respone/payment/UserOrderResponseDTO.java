package com.ctu.bookstore.dto.respone.payment;

import com.ctu.bookstore.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserOrderResponseDTO {
    Long id;

    String userId;

    Set<OrderItemResponseDTO> orderItems = new HashSet<>();
    String stripeSessionId;
    Double totalAmount;

    OrderStatus status;
    String shippingAddress;

    LocalDateTime orderDate;
    String notion;
    String paymentMethod;
    String voucher;

}
