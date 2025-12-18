package com.ctu.bookstore.dto.respone.display;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    String id;
    String productId;
    String userId;
    String username;

    String comment;
    Integer rating;
    Boolean verifiedPurchase;

    Instant createdAt;
}