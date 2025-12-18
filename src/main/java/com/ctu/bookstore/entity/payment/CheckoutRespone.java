package com.ctu.bookstore.entity.payment;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter //recommend using @Getter and @Setter cho entity(google for more). One reason is it easy to manage
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckoutRespone {
    String stripeCheckoutUrl;
}
