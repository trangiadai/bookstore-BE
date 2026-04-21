package com.ctu.bookstore.service.display;

import com.ctu.bookstore.dto.request.display.CartItemRequestDTO;
import com.ctu.bookstore.entity.identity.User;
import com.ctu.bookstore.entity.display.Cart;
import com.ctu.bookstore.entity.display.CartItem;
import com.ctu.bookstore.entity.display.Product;
import com.ctu.bookstore.mapper.display.CartMapper;
import com.ctu.bookstore.repository.identity.UserRepository;
import com.ctu.bookstore.repository.display.CartItemRepository;
import com.ctu.bookstore.repository.display.CartRepository;
import com.ctu.bookstore.repository.display.ProductRepository;
import com.ctu.bookstore.service.identity.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class CartService {
    UserRepository userRepository;
    CartRepository cartRepository;
    ProductRepository productRepository;
    CartItemRepository cartItemRepository;
    UserService userService;
    CartMapper cartMapper;

    public Cart addOrUpdateCart(String userId, CartItemRequestDTO cartItemRequestDTO){
        User user = userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User not exist to create cart"));
//        Cart cart = cartRepository.findByUserId(userId).orElseGet(
//                ()->Cart.builder()
//                        .user(user)
////                            .createdAt(LocalDateTime.now())
////                            .updatedAt(LocalDateTime.now())
//                        .cartItems(new HashSet<>())
//                        .build()
//        );
        Cart cart = cartRepository.findByUserId(userId).orElse(null);

        if (cart == null) {
            cart = Cart.builder()
                    .user(user)
                    .cartItems(new HashSet<>())
                    .build();

            cart = cartRepository.save(cart);
        }
        Product product = productRepository.findById(cartItemRequestDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        if (product.getQuantity() < cartItemRequestDTO.getQuantity()) {
            throw new RuntimeException("Số lượng tồn kho không đủ.");
        }
        CartItem existingItem = cart.getCartItems().stream()
                .filter(item->item.getProduct().getId().equals(cartItemRequestDTO.getProductId()))
                .findFirst()
                .orElse(null);
        if(existingItem!=null){
            existingItem.setQuatity(existingItem.getQuatity()+ cartItemRequestDTO.getQuantity());
        }else {
            CartItem newItem = CartItem.builder()
                    .product(product)
                    .cart(cart)
                    .quatity(cartItemRequestDTO.getQuantity())
                    .build();
            cart.getCartItems().add(newItem);
        }

        return cartRepository.save(cart) ;

    }

    public Cart getMyCart(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("khong có user trong Cart service"));
        return cartRepository.findByUserId(user.getId()).orElseThrow(()->new RuntimeException("không tìm thấy cart trong cart service"));


    }

    public int getSumMyCart() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("không có user trong Cart service"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("không tìm thấy cart trong cart service"));

        return cart.getCartItems().size();
    }

    @Transactional
    public Cart updateItemInCart(String productId, int quantity) {
        // Lấy user hiện tại từ security context
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user trong cartService"));

        // Lấy cart của user
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cart của user"));

        // Tìm CartItem theo productId
        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ"));

        if (quantity < 0) {
            throw new RuntimeException("Số lượng không hợp lệ");
        }

        if (quantity == 0) {
            // Xóa item khỏi cart
            cart.getCartItems().remove(cartItem);
        } else {
            // Kiểm tra tồn kho
            if (cartItem.getProduct().getQuantity() < quantity) {
                throw new RuntimeException("Số lượng tồn kho không đủ.");
            }
            cartItem.setQuatity(quantity); // cập nhật số lượng
        }

        // Lưu cart
        cartRepository.save(cart);


        return cart;
    }

@Transactional
public Cart incrementItem(String productId) {

    // Lấy user từ Security
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

    // Lấy Cart của user
    Cart cart = cartRepository.findByUserId(user.getId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy cart"));

    // 🔥 Tìm CartItem theo cartId + productId (CHỈ TRUY VẤN 1 ITEM)
    CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
            .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ"));

    // Kiểm tra tồn kho
    int newQuantity = item.getQuatity() + 1;
    if (item.getProduct().getQuantity() < newQuantity) {
        throw new RuntimeException("Số lượng trong kho không đủ");
    }

    // Tăng số lượng
    item.setQuatity(newQuantity);
    cartItemRepository.save(item); // chỉ cần save item, không cần save cả cart

    return cart; // nếu bạn trả cart về FE để hiển thị lại
}
    public CartItem increaseQuantity(String cartId, String productId) {

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ"));

        cartItem.setQuatity(cartItem.getQuatity() + 1);

        cartItemRepository.save(cartItem);

        return cartItem;
    }


    @Transactional
    public Cart decrementItem(String productId) {
        // Lấy user từ Security
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        // Lấy cart
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cart"));

        // Tìm cartItem nhanh hơn bằng repository (không cần stream)
        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ"));

        // Nếu giảm số lượng xuống 0 → xóa
        if (item.getQuatity() <= 1) {
            cart.getCartItems().remove(item);  // orphanRemoval = true → tự xóa DB
        } else {
            item.setQuatity(item.getQuatity() - 1);
        }

        // Lưu cart
        cartRepository.save(cart);

        return cart;
    }

    public CartItem decreaseQuantity(String cartId, String productId) {

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ trong cart service"));

        // Nếu số lượng ≤ 1 thì xóa luôn khỏi giỏ
        if (cartItem.getQuatity() <= 1) {
            cartItemRepository.delete(cartItem);
            return null; // hoặc trả response đặc biệt
        }

        cartItem.setQuatity(cartItem.getQuatity() - 1);
        cartItemRepository.save(cartItem);

        return cartItem;
    }

    public void delete(String id) {
        // Lấy cart từ DB
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cart với id: " + id));

        // Force load cartItems để tránh LazyInitializationException
        if (cart.getCartItems() != null) {
            cart.getCartItems().forEach(item -> item.setCart(cart)); // đảm bảo mỗi CartItem vẫn link Cart
            cart.getCartItems().clear(); // xóa tất cả CartItem khỏi Set, orphanRemoval sẽ xóa DB
        }

        // Xóa Cart
//        cartRepository.delete(cart);
    }

    public void deleteCartItem(String idCartItem){
//        String name = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(name).orElseThrow(()->new RuntimeException("Khong tìm thấy user trong cart service"));
//        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(()-> new RuntimeException("khong tìm tháy cart trong cart service"));
        CartItem cartItem = cartItemRepository.findById(idCartItem).orElseThrow(()->new RuntimeException("không ctimf thấy cart item trong cart service"));
        cartItemRepository.delete(cartItem);
    }

    //    @Transactional
//    public Cart incrementItem(String productId) {
//        // Lấy user từ Security
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
//
//        // Lấy Cart của user
//        Cart cart = cartRepository.findByUserId(user.getId())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy cart"));
//
//        // Tìm CartItem theo productId
//        CartItem item = cart.getCartItems().stream()
//                .filter(ci -> ci.getProduct().getId().equals(productId))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ"));
//
//        // Kiểm tra tồn kho
//        int newQuantity = item.getQuatity() + 1;
//        if (item.getProduct().getQuantity() < newQuantity) {
//            throw new RuntimeException("Số lượng trong kho không đủ");
//        }
//
//        item.setQuatity(newQuantity);
//
//        cartRepository.save(cart);
//
//        return cart;
//    }
//    @Transactional
//    public Cart decrementItem(String productId) {
//        // Lấy user
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
//
//        // Lấy cart
//        Cart cart = cartRepository.findByUserId(user.getId())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy cart"));
//
//        // Tìm CartItem
//        CartItem item = cart.getCartItems().stream()
//                .filter(ci -> ci.getProduct().getId().equals(productId))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ"));
//
//        // Nếu giảm còn 0 → xóa item
//        if (item.getQuatity() - 1 <= 0) {
//            cart.getCartItems().remove(item); // orphanRemoval sẽ tự xóa DB
//        } else {
//            item.setQuatity(item.getQuatity() - 1);
//        }
//
//        cartRepository.save(cart);
//
//        return cart;
//    }
}
