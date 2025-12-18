package com.ctu.bookstore.entity.payment;

import com.ctu.bookstore.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Entity
@Getter //recommend using @Getter and @Setter cho entity(google for more). One reason is it easy to manage
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InforCheckout {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String name;
    String email;
    String phoneNumber;
    String adress;
    String voucher;
    String note;
}
