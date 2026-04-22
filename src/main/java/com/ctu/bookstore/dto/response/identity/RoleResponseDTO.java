package com.ctu.bookstore.dto.response.identity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponseDTO {
    String name;
    String description;
    Set<PermissionResponseDTO> permissions;
}
