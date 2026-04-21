package com.ctu.bookstore.dto.response.identity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResposeDTO {
    String name;
    String description;
    Set<PermissionResponeDTO> permissions;
}
