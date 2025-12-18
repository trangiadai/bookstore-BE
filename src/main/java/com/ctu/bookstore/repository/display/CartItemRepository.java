package com.ctu.bookstore.repository.display;

import com.ctu.bookstore.entity.display.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,String> {
    Optional<CartItem> findByCartIdAndProductId(String cartId, String productId);


}
