package com.ctu.bookstore.entity.display;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class ProductImages {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String Url;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id") // Đặt tên cột khóa ngoại trong DB
    @JsonIgnore
    private Product product;
}