package com.ctu.bookstore.dto.response.display;

import com.ctu.bookstore.entity.display.ProductImages;
import lombok.Data;

import java.util.Date;
import java.util.Set;
@Data
public class ProductResponseDTO {
    String id;
    String nameProduct;
    Double importPrice;
    Double sellingPrice;
    Double salePrice;
    int quantity;
    String description;
    String author;
    Date createDate;
    String categoryName;

    Double averageStars;    // trung bình sao (1-5)
    Integer ratingCount;    // tổng số lượt rating
    Set<ProductImages> imagesUrl;
//    List<String> commentId;


}