package com.ctu.bookstore.repository.display;

import com.ctu.bookstore.entity.display.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,String> {
    Optional<Cart> findByUserId(String userId);
}
