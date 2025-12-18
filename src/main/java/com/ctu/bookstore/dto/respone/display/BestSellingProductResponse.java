package com.ctu.bookstore.dto.respone.display;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BestSellingProductResponse {
    String productId;
    String nameProduct;

    Long totalQuantity;
    Double totalRevenue;

}
