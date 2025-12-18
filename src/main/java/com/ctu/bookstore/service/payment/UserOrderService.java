package com.ctu.bookstore.service.payment;

import com.ctu.bookstore.dto.respone.display.BestSellingProductResponse;
import com.ctu.bookstore.dto.respone.payment.UserOrderResponse;
import com.ctu.bookstore.entity.payment.UserOrder;
import com.ctu.bookstore.enums.OrderStatus;
import com.ctu.bookstore.mapper.payment.UserOrderMapper;
import com.ctu.bookstore.repository.payment.OrderItemRepository;
import com.ctu.bookstore.repository.payment.UserOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserOrderService {
    private final UserOrderRepository userOrderRepository;
    private final UserOrderMapper userOrderMapper;
    private final OrderItemRepository orderItemRepository;

    public List<UserOrderResponse> getAllOrders(){
        return userOrderRepository.findAll()
                .stream()
                .map(userOrder -> userOrderMapper.toUserOrderResponse(userOrder))
                .toList();
    }

    public List<UserOrderResponse> getOrdersByUserId(String userId) {
        return userOrderRepository
                .findByUserIdOrderByOrderDateDesc(userId)
                .stream()
                .map(userOrderMapper::toUserOrderResponse)
                .toList();
    }

    public UserOrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {

        UserOrder order = userOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        order.setStatus(newStatus);

        userOrderRepository.save(order);

        return userOrderMapper.toUserOrderResponse(order);
    }
    public List<BestSellingProductResponse> getBestSellingProducts(int limit) {
        // Chỉ tính những đơn đã thanh toán / đã giao
        List<OrderStatus> validStatuses = List.of(
                OrderStatus.PAID,
                OrderStatus.SHIPPED,
                OrderStatus.DELIVERED
        );

        PageRequest pageRequest = PageRequest.of(0, limit); // top "limit" sản phẩm
        return orderItemRepository
                .findBestSellingProducts(validStatuses, pageRequest)
                .getContent();
    }
}
