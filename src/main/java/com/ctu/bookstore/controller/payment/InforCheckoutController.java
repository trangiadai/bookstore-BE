package com.ctu.bookstore.controller.payment;

import com.ctu.bookstore.dto.request.payment.InforCheckoutRequestUpdateDTO;
import com.ctu.bookstore.dto.respone.ApiResponeDTO;
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
    public ApiResponeDTO<InforCheckout> getInforCheckout(){
        return ApiResponeDTO.<InforCheckout>builder()
                .result(inforCheckoutService.getMyInforCheckout())
                .build();
    }

    @PutMapping
    public ApiResponeDTO<InforCheckout> updateInforCheckout(@RequestBody InforCheckoutRequestUpdateDTO requestUpdate){
        return ApiResponeDTO.<InforCheckout>builder()
                .result(inforCheckoutService.updateInfo(requestUpdate))
                .build();
    }
}
