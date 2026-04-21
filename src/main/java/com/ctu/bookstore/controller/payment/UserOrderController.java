package com.ctu.bookstore.controller.payment;

import com.ctu.bookstore.dto.response.ApiResponseDTO;
import com.ctu.bookstore.dto.response.display.BestSellingProductResponseDTO;
import com.ctu.bookstore.dto.response.payment.UserOrderResponseDTO;
import com.ctu.bookstore.enums.OrderStatus;
import com.ctu.bookstore.repository.identity.UserRepository;
import com.ctu.bookstore.service.payment.UserOrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/orders")
public class UserOrderController {
    UserOrderService userOrderService;
    UserRepository userRepository;

    @GetMapping
    public ApiResponseDTO<List<UserOrderResponseDTO>> getAllOrders(){
        return ApiResponseDTO.<List<UserOrderResponseDTO>>builder()
                .result(userOrderService.getAllOrders())
                .build();
    }

    @GetMapping
    public ApiResponseDTO<List<UserOrderResponseDTO>> getOrders() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(name);
        return ApiResponseDTO.<List<UserOrderResponseDTO>>builder()
                .result(userOrderService.getOrdersByUserId(user.get().getId()))
                .build();
    }

    @PutMapping("/{orderId}")
    public ApiResponseDTO<UserOrderResponseDTO> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status
    ) {
        UserOrderResponseDTO response = userOrderService.updateOrderStatus(orderId, status);
        return ApiResponseDTO.<UserOrderResponseDTO>builder()
                .result(response)
                .build();
    }

    @GetMapping("/best-selling-products")
    public ApiResponseDTO<List<BestSellingProductResponseDTO>> getBestSellingProducts(
            @RequestParam(defaultValue = "10") int limit  // top 10 mặc định
    ) {
        return ApiResponseDTO.<List<BestSellingProductResponseDTO>>builder()
                .result(userOrderService.getBestSellingProducts(limit))
                .build();
    }
}
