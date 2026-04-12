package com.ctu.bookstore.controller.payment;

import com.ctu.bookstore.dto.respone.ApiResponeDTO;
import com.ctu.bookstore.dto.respone.display.BestSellingProductResponseDTO;
import com.ctu.bookstore.dto.respone.payment.UserOrderResponseDTO;
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
    public ApiResponeDTO<List<UserOrderResponseDTO>> getAllOrders(){
        return ApiResponeDTO.<List<UserOrderResponseDTO>>builder()
                .result(userOrderService.getAllOrders())
                .build();
    }

    @GetMapping
    public ApiResponeDTO<List<UserOrderResponseDTO>> getOrders() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(name);
        return ApiResponeDTO.<List<UserOrderResponseDTO>>builder()
                .result(userOrderService.getOrdersByUserId(user.get().getId()))
                .build();
    }

    @PutMapping("/{orderId}")
    public ApiResponeDTO<UserOrderResponseDTO> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status
    ) {
        UserOrderResponseDTO response = userOrderService.updateOrderStatus(orderId, status);
        return ApiResponeDTO.<UserOrderResponseDTO>builder()
                .result(response)
                .build();
    }

    @GetMapping("/best-selling-products")
    public ApiResponeDTO<List<BestSellingProductResponseDTO>> getBestSellingProducts(
            @RequestParam(defaultValue = "10") int limit  // top 10 mặc định
    ) {
        return ApiResponeDTO.<List<BestSellingProductResponseDTO>>builder()
                .result(userOrderService.getBestSellingProducts(limit))
                .build();
    }
}
