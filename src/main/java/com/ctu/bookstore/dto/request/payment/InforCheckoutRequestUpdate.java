package com.ctu.bookstore.dto.request.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InforCheckoutRequestUpdate {
    String name;
    String email;
    String phoneNumber;
    String adress;
    String voucher;
    String note;
}
