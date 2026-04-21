package com.ctu.bookstore.repository.payment;

import com.ctu.bookstore.dto.response.display.BestSellingProductResponseDTO;
import com.ctu.bookstore.entity.payment.OrderItem;
import com.ctu.bookstore.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("""
        SELECT new com.ctu.bookstore.dto.respone.display.BestSellingProductResponse(
            oi.product.id,
            oi.product.nameProduct,
            SUM(oi.quantity),
            SUM(oi.quantity * oi.priceAtTime)
        )
        FROM OrderItem oi
        WHERE oi.order.status IN :statuses
        GROUP BY oi.product.id, oi.product.nameProduct
        ORDER BY SUM(oi.quantity) DESC
        """)
    List<BestSellingProductResponseDTO> findBestSellingProducts(@Param("statuses") List<OrderStatus> statuses);

    // Nếu muốn giới hạn số lượng kết quả (top N) thì dùng Pageable:
    @Query("""
        SELECT new com.ctu.bookstore.dto.respone.display.BestSellingProductResponse(
            oi.product.id,
            oi.product.nameProduct,
            SUM(oi.quantity),
            SUM(oi.quantity * oi.priceAtTime)
        )
        FROM OrderItem oi
        WHERE oi.order.status IN :statuses
        GROUP BY oi.product.id, oi.product.nameProduct
        ORDER BY SUM(oi.quantity) DESC
        """)
    Page<BestSellingProductResponseDTO> findBestSellingProducts(
            @Param("statuses") List<OrderStatus> statuses,
            Pageable pageable
    );
}