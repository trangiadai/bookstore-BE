package com.ctu.bookstore.dto.request.display;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRequestDTO {
    Long orderId;    // Đơn hàng dùng để verify đã mua sản phẩm
    String productId;
    String comment;
    @NotNull
    @Min(1)
    @Max(5)
    Integer rating;
}