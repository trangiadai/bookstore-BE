package com.ctu.bookstore.dto.request.payment;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckoutRequestDTO {
    // List này chứa các sản phẩm mà người dùng muốn thanh toán.
    // Nếu list này RỖNG hoặc NULL, ta sẽ mặc định lấy TẤT CẢ sản phẩm từ GIỎ HÀNG (Cart) của User.
    List<CheckoutItemRequestDTO> itemsToCheckout;
    boolean isCartCheckout = false;
    // Bạn có thể thêm các trường khác như:
    // String shippingAddressId;
    // String couponCode;
}
