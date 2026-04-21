package com.ctu.bookstore.dto.response.display;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponseDTO {
    String id;
    String productId;
    String userId;
    String username;

    String comment;
    Integer rating;
    Boolean verifiedPurchase;

    Instant createdAt;
}