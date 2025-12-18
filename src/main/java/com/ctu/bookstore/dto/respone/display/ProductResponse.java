package com.ctu.bookstore.dto.respone.display;

import com.ctu.bookstore.entity.display.ProductImages;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;
@Data
public class ProductResponse {
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