package com.ctu.bookstore.dto.request.identity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequestDTO {
    String name;
    String description;
    Set<String> permissions;
}
