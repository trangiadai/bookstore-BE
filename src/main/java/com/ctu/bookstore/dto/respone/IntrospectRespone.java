package com.ctu.bookstore.dto.respone;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntrospectRespone {
    boolean valid;
    String userName; // dùng để lưu sessionId trong socketHandler
}
