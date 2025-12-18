package com.ctu.bookstore.repository.display;

import com.ctu.bookstore.entity.display.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImagesRepository extends JpaRepository<ProductImages,String> {
}