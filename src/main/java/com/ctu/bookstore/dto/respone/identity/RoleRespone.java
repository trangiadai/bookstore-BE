package com.ctu.bookstore.dto.respone.identity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRespone {
    String name;
    String description;
    Set<PermissionRespone> permissions;
}
