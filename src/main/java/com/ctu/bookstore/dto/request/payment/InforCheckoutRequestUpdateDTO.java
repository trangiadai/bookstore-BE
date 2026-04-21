package com.ctu.bookstore.dto.request.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InforCheckoutRequestUpdateDTO {
    @NotBlank(message = "name của người nhận không được bỏ trống")
    String name;
    String email;
    @NotBlank(message = "phoneNumber của người nhận không được bỏ trống")
    @Pattern(regexp = "^\\d{10}", message = "phoneNumer invalid format, phoneNumber must be 10 character")
    String phoneNumber;
    @NotBlank(message = "address của người nhận không được bỏ trống")
    String address;
    String voucher;
    String note;
}
