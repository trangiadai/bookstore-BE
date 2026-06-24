package com.ctu.bookstore.service.payment;

import com.ctu.bookstore.dto.request.payment.CheckoutItemRequestDTO;
import com.ctu.bookstore.dto.response.payment.UserOrderResponseDTO;
import com.ctu.bookstore.entity.identity.User;
import com.ctu.bookstore.entity.display.Cart;
import com.ctu.bookstore.entity.display.CartItem;
import com.ctu.bookstore.entity.display.Product;
import com.ctu.bookstore.entity.payment.CheckoutRespone;
import com.ctu.bookstore.entity.payment.InforCheckout;
import com.ctu.bookstore.entity.payment.UserOrder;
import com.ctu.bookstore.entity.payment.OrderItem;
import com.ctu.bookstore.enums.OrderStatus;
import com.ctu.bookstore.mapper.payment.UserOrderMapper;
import com.ctu.bookstore.repository.identity.UserRepository;
import com.ctu.bookstore.repository.display.CartRepository;
import com.ctu.bookstore.repository.display.ProductRepository;
import com.ctu.bookstore.repository.payment.OrderRepository;
import com.ctu.bookstore.service.display.CartService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    UserRepository userRepository;
    CartRepository cartRepository;
    ProductRepository productRepository;
    OrderRepository orderRepository;
    @NonFinal
    @Value("${stripe.apiKey}")
    private String stripeSecretKey;
    CartService cartService;
    UserOrderMapper userOrderMapper;

    @Transactional
    public CheckoutRespone createCheckoutSession(String userId) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại."));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng rỗng, không thể thanh toán.");
        }

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        User user = cart.getUser();
        InforCheckout inforCheckout = user.getInforCheckout();
        Double totalAmount = 30000.0;

        for (CartItem item : cart.getCartItems()) {
            // Kiểm tra số lượng tồn kho lần cuối
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm đã bị xóa."));

            if (item.getProduct().getQuantity()<item.getQuatity()) {
                throw new RuntimeException("Sản phẩm '" + product.getNameProduct() + "' không đủ tồn kho.");
            }

            // Stripe nhận giá dưới dạng đơn vị nhỏ nhất (cents/đồng)
//            long unitAmount = (long) (item.getProduct().getSellingPrice()*100);
            long unitAmount = Math.round(item.getProduct().getSellingPrice());
            System.out.println("totalAmout trong Payment service tạo session: "+unitAmount);
            totalAmount += item.getProduct().getSellingPrice() * item.getQuatity();
            System.out.println("totalAmout trong Payment service tạo session: "+totalAmount);
            product.setQuantity(product.getQuantity()-item.getQuatity());
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity((long) item.getQuatity())
                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("vnd") // Hoặc "usd" tùy theo cấu hình
                            .setUnitAmount(unitAmount)
                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(item.getProduct().getNameProduct())
                                    .build())
                            .build())
                    .build();
            lineItems.add(lineItem);
        }
        // Tạo Đơn hàng TẠM THỜI với trạng thái PENDING
        UserOrder newOrder = UserOrder.builder()
                .user(user)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .shippingAddress(user.getInforCheckout().getAddress())// Cần thêm logic lấy địa chỉ
                .orderDate(LocalDateTime.now())
                .paymentMethod("Credit Card")
                .phoneNumber(inforCheckout.getPhoneNumber())
                .notion(inforCheckout.getNote())
                .voucher(inforCheckout.getVoucher())
                .build();

        // Thêm OrderItem
        for (CartItem item : cart.getCartItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(newOrder)
                    .product(item.getProduct())
                    .quantity(item.getProduct().getQuantity())
                    .priceAtTime(item.getProduct().getSellingPrice())
                    .build();
            newOrder.getOrderItems().add(orderItem);
        }

        newOrder = orderRepository.save(newOrder);


        // 2. Tạo Session Params
        SessionCreateParams params = SessionCreateParams.builder()
                .addAllLineItem(lineItems)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                // Dùng ID Đơn hàng để nhận diện khi Stripe gửi webhook/redirect về
                .putMetadata("orderId", newOrder.getId().toString())
                .setSuccessUrl("http://localhost:3000/payment/success")
                .setCancelUrl("http://localhost:3000/payment/failed")
                .build();

        // 3. Gọi API Stripe để tạo Session
        Session session = Session.create(params);

        // Lưu Session ID vào Order TẠM THỜI để cập nhật sau
        newOrder.setStripeSessionId(session.getId());
        orderRepository.save(newOrder);
        System.out.println("id cart trong payment service: "+ cart.getId());
        cartService.delete(cart.getId());

        return CheckoutRespone.builder()
                .stripeCheckoutUrl(session.getUrl())
                .build();
    }

    public UserOrderResponseDTO BuyByShipCOD(String userId){
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại."));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng rỗng, không thể thanh toán.");
        }

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        User user = cart.getUser();
        Double totalAmount = 0.0;


        for (CartItem item : cart.getCartItems()) {
            // Kiểm tra số lượng tồn kho lần cuối
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm đã bị xóa."));

            if ( item.getProduct().getQuantity()<item.getQuatity()) {
                System.out.println("quantity: "+item.getQuatity());
                System.out.println("quantity: "+item.getProduct().getQuantity());
                throw new RuntimeException("Sản phẩm '" + product.getNameProduct() + "' không đủ tồn kho.");
            }


            totalAmount += item.getProduct().getSellingPrice() * item.getQuatity();
            product.setQuantity(product.getQuantity()-item.getQuatity());

        }
        // Tạo Đơn hàng TẠM THỜI với trạng thái PENDING
        UserOrder newOrder = UserOrder.builder()
                .user(user)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .shippingAddress(user.getInforCheckout().getAddress())// Cần thêm logic lấy địa chỉ
                .orderDate(LocalDateTime.now())
                .paymentMethod("Ship COD")
                .build();

        // Thêm OrderItem
        for (CartItem item : cart.getCartItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(newOrder)
                    .product(item.getProduct())
                    .quantity(item.getProduct().getQuantity())
                    .priceAtTime(item.getProduct().getSellingPrice())
                    .build();
            newOrder.getOrderItems().add(orderItem);
        }
        cartService.delete(cart.getId());
        newOrder = orderRepository.save(newOrder);
        return userOrderMapper.toUserOrderResponse(newOrder);
    }

    @Transactional
    public UserOrderResponseDTO createCheckoutBySelectedItems(List<CheckoutItemRequestDTO> selectedItems) {

        // Lấy user hiện tại
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // Lấy cart của user
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));

        if (selectedItems == null || selectedItems.isEmpty()) {
            throw new RuntimeException("Danh sách sản phẩm thanh toán trống");
        }

        // Tạo map productId → quantity user muốn mua
        Map<String, Integer> quantityMap = selectedItems.stream()
                .collect(Collectors.toMap(CheckoutItemRequestDTO::getProductId, CheckoutItemRequestDTO::getQuantity));

        // Lọc ra các cartItem liên quan
        List<CartItem> itemsToBuy = cart.getCartItems().stream()
                .filter(item -> quantityMap.containsKey(item.getProduct().getId()))
                .collect(Collectors.toList());

        if (itemsToBuy.isEmpty()) {
            throw new RuntimeException("Không có sản phẩm nào hợp lệ để thanh toán");
        }

        double totalAmount = 0.0;

        // Kiểm tra tồn kho + số lượng hợp lệ
        for (CartItem cartItem : itemsToBuy) {
            int buyQuantity = quantityMap.get(cartItem.getProduct().getId());

            if (buyQuantity <= 0) {
                throw new RuntimeException("Số lượng mua không hợp lệ");
            }

            if (buyQuantity > cartItem.getQuatity()) {
                throw new RuntimeException("Số lượng mua vượt quá số lượng trong giỏ");
            }

            if (buyQuantity > cartItem.getProduct().getQuantity()) {
                throw new RuntimeException("Sản phẩm '" + cartItem.getProduct().getNameProduct() + "' không đủ tồn kho");
            }

            totalAmount += cartItem.getProduct().getSellingPrice() * buyQuantity;
        }

        // Tạo Order
        UserOrder order = UserOrder.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .paymentMethod(selectedItems.get(0).getPaymentMethod())   // lấy từ request
                .shippingAddress("Địa chỉ mẫu") // TODO: tự lấy từ user sau này
                .totalAmount(totalAmount)
                .build();

        // Thêm OrderItem
        for (CartItem cartItem : itemsToBuy) {

            int buyQuantity = quantityMap.get(cartItem.getProduct().getId());

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .quantity(buyQuantity)
                    .priceAtTime(cartItem.getProduct().getSellingPrice())
                    .build();

            order.getOrderItems().add(orderItem);

            // Cập nhật lại giỏ hàng sau khi mua
            if (buyQuantity == cartItem.getQuatity()) {
                // mua hết → xóa khỏi cart
                cart.getCartItems().remove(cartItem);
            } else {
                // mua 1 phần → trừ số lượng
                cartItem.setQuatity(cartItem.getQuatity() - buyQuantity);
            }
        }

        // Lưu order + cart
        orderRepository.save(order);
        cartRepository.save(cart);

        return userOrderMapper.toUserOrderResponse(order);
    }
}