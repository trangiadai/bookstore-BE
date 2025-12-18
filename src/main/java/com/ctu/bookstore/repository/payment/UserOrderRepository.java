package com.ctu.bookstore.repository.payment;

import com.ctu.bookstore.entity.payment.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {
    List<UserOrder> findByUserIdOrderByOrderDateDesc(String userId);
    Optional<UserOrder> findByIdAndUserId(Long id, String userId);

}
