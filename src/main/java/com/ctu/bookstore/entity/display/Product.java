package com.ctu.bookstore.entity.display;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Product {
    @Id
    String id;
    String nameProduct;
    Double importPrice;
    Double sellingPrice;
    Double salePrice;
    int quantity;
    String description;
    String author;
    Instant createDate;
    // ⭐ thêm 2 field này
    Double averageStars;    // trung bình sao (1-5)
    Integer ratingCount;    // tổng số lượt rating
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;
    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )

    Set<ProductImages> imagesUrl = new HashSet<>();

    // Helper method (giữ nguyên để đảm bảo mối quan hệ hai chiều)
    public void addImage(ProductImages image) {
        if (this.imagesUrl == null) {
            this.imagesUrl = new HashSet<>();
        }
        this.imagesUrl.add(image);
        image.setProduct(this);
    }


}