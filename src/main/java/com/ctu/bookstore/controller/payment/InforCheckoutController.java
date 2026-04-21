package com.ctu.bookstore.controller.payment;

import com.ctu.bookstore.dto.request.payment.InforCheckoutRequestUpdateDTO;
import com.ctu.bookstore.dto.response.ApiResponseDTO;
import com.ctu.bookstore.entity.payment.InforCheckout;
import com.ctu.bookstore.service.payment.InforCheckoutService;
import jakarta.validation.Valid;
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
    public ApiResponseDTO<InforCheckout> getInforCheckout(){
        return ApiResponseDTO.<InforCheckout>builder()
                .result(inforCheckoutService.getMyInforCheckout())
                .build();
    }

    @PutMapping
    public ApiResponseDTO<InforCheckout> updateInforCheckout(@Valid @RequestBody InforCheckoutRequestUpdateDTO requestUpdate){
        return ApiResponseDTO.<InforCheckout>builder()
                .result(inforCheckoutService.updateInfo(requestUpdate))
                .build();
    }
}
