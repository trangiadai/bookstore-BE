package com.ctu.bookstore.dto.response.display;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BestSellingProductResponseDTO {
    String productId;
    String nameProduct;

    Long totalQuantity;
    Double totalRevenue;

}
