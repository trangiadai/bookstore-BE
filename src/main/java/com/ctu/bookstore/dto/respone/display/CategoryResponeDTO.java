package com.ctu.bookstore.dto.respone.display;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponeDTO {
    String id;
    String nameCategory;
//    private String slug;

    // Thông tin cha
    String parentId ;
    String parentName ; // Giúp UI hiển thị dễ dàng

    // Danh sách các danh mục con (giúp xây dựng cấu trúc cây/lồng nhau)
    Set<CategoryResponeDTO> children = new HashSet<>();
}
