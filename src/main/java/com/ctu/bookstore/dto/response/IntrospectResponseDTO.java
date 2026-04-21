package com.ctu.bookstore.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntrospectResponseDTO {
    boolean valid;
    String userName; // dùng để lưu sessionId trong socketHandler
}
