package com.ctu.bookstore.controller.payment;

import com.ctu.bookstore.dto.request.payment.InforCheckoutRequestUpdate;
import com.ctu.bookstore.dto.respone.ApiRespone;
import com.ctu.bookstore.entity.payment.InforCheckout;
import com.ctu.bookstore.service.payment.InforCheckoutService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/info-checkout")
public class InforCheckoutController {
    InforCheckoutService inforCheckoutService;

    @GetMapping
    public ApiRespone<InforCheckout> getInforCheckout(){
        return ApiRespone.<InforCheckout>builder()
                .result(inforCheckoutService.getMyInforCheckout())
                .build();
    }

    @PutMapping("/update")
    public ApiRespone<InforCheckout> updateInforCheckout(@RequestBody InforCheckoutRequestUpdate requestUpdate){
        return ApiRespone.<InforCheckout>builder()
                .result(inforCheckoutService.updateInfo(requestUpdate))
                .build();
    }
}
