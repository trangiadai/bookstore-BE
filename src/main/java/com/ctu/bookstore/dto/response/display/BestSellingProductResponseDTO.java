package com.ctu.bookstore.dto.response.display;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BestSellingProductResponseDTO {
    String productId;
    String nameProduct;

    Long totalQuantity;
    Double totalRevenue;

}
