package com.ctu.bookstore.repository.payment;

import com.ctu.bookstore.entity.payment.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<UserOrder, Long> {
}
