package com.ctu.bookstore.repository.display;

import com.ctu.bookstore.entity.display.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,String> {
    //    Page<Product> findAllProducts(Pageable pageable);
    Page<Product> findBySellingPriceBetween(
            Double minPrice,
            Double maxPrice,
            Pageable pageable
    );
    Page<Product> findByCategory_Id(String categoryId, Pageable pageable);
    // ⭐ filter theo rating
    Page<Product> findByAverageStarsBetween(
            Double minStars,
            Double maxStars,
            Pageable pageable
    );
    Page<Product> findByAverageStarsGreaterThanEqual(
            Double minStars,
            Pageable pageable
    );
}