package com.ctu.bookstore.dto.request.display;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequestDTO {
    @NotBlank(message = "nameProduct của sản phẩm không đựợc để trống")
    String nameProduct;
    Double importPrice;
    @NotNull(message = "Giá đang bán không được để trống")
    Double sellingPrice;
    Double salePrice;
    int quantity;
    String description;
    String author;
    String categoryId;
    List<MultipartFile> images;
}
