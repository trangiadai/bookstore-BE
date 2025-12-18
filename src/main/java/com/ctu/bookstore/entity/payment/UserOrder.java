package com.ctu.bookstore.entity.payment;

import com.ctu.bookstore.entity.identity.User;
import com.ctu.bookstore.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter //recommend using @Getter and @Setter cho entity(google for more). One reason is it easy to manage
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;
    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<OrderItem> orderItems = new HashSet<>();
    String stripeSessionId;
    Double totalAmount;
    @Enumerated(EnumType.STRING)
    OrderStatus status;
    String shippingAddress;
    @Column(columnDefinition = "DATETIME")
    LocalDateTime orderDate;
    String phoneNumber;
    String notion;
    String paymentMethod;
    String voucher;



}
