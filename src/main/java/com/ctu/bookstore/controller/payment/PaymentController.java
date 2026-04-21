package com.ctu.bookstore.controller.payment;

import com.ctu.bookstore.dto.response.ApiResponseDTO;
import com.ctu.bookstore.dto.response.payment.UserOrderResponseDTO;
import com.ctu.bookstore.entity.identity.User;
import com.ctu.bookstore.entity.payment.CheckoutRespone;
import com.ctu.bookstore.repository.identity.UserRepository;
import com.ctu.bookstore.service.payment.PaymentService;
import com.stripe.exception.StripeException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/checkout")
public class PaymentController {
    PaymentService paymentService;
    UserRepository userRepository;

    @PostMapping("/create-session")
    public ApiResponseDTO<CheckoutRespone> createCheckoutSession() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("User không tồn tại trong payment controller"));
        try {
            CheckoutRespone response = paymentService.createCheckoutSession(user.getId());
            return ApiResponseDTO.<CheckoutRespone>builder()
                    .result(response)
                    .build();
        } catch (StripeException e) {
            // Xử lý lỗi API Stripe
            throw new RuntimeException("Lỗi Stripe: " + e.getMessage());
        }
    }

    @PostMapping("/create-shipCOD")
    public ApiResponseDTO<UserOrderResponseDTO> createShipCOD() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("User không tồn tại trong payment controller"));
        try {
            UserOrderResponseDTO response = paymentService.BuyByShipCOD(user.getId());
            return ApiResponseDTO.<UserOrderResponseDTO>builder()
                    .result(response)
                    .build();
        } catch (RuntimeException e) {
            // Xử lý lỗi API Stripe
            throw new RuntimeException("Lỗi khi gọi api create-shipCOD"+ e);
        }
    }
}