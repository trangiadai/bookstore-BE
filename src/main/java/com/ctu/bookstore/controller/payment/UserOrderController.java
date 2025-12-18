package com.ctu.bookstore.controller.payment;

import com.ctu.bookstore.dto.respone.ApiRespone;
import com.ctu.bookstore.dto.respone.display.BestSellingProductResponse;
import com.ctu.bookstore.dto.respone.payment.UserOrderResponse;
import com.ctu.bookstore.enums.OrderStatus;
import com.ctu.bookstore.repository.identity.UserRepository;
import com.ctu.bookstore.service.payment.UserOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class UserOrderController {
    private final UserOrderService userOrderService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ApiRespone<List<UserOrderResponse>> getAllOrders(){
        return ApiRespone.<List<UserOrderResponse>>builder()
                .result(userOrderService.getAllOrders())
                .build();
    }

    @GetMapping("/user")
    public ApiRespone<List<UserOrderResponse>> getOrders() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(name);
        return ApiRespone.<List<UserOrderResponse>>builder()
                .result(userOrderService.getOrdersByUserId(user.get().getId()))
                .build();
    }
    @PutMapping("/{orderId}/status")
    public ApiRespone<UserOrderResponse> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status
    ) {
        UserOrderResponse response = userOrderService.updateOrderStatus(orderId, status);
        return ApiRespone.<UserOrderResponse>builder()
                .result(response)
                .build();
    }
    @GetMapping("/best-selling-products")
    public ApiRespone<List<BestSellingProductResponse>> getBestSellingProducts(
            @RequestParam(defaultValue = "10") int limit  // top 10 mặc định
    ) {
        return ApiRespone.<List<BestSellingProductResponse>>builder()
                .result(userOrderService.getBestSellingProducts(limit))
                .build();
    }
}
