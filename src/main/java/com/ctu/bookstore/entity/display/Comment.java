package com.ctu.bookstore.entity.display;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @MongoId
    String id;

    String productId;
    String userId;
    String username;
    String comment;
    Integer rating;        // 1 - 5
    Instant createdAt;
}