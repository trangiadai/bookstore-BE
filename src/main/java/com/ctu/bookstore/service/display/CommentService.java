package com.ctu.bookstore.service.display;

import com.ctu.bookstore.dto.request.display.CommentRequestDTO;
import com.ctu.bookstore.dto.response.display.CommentResponseDTO;
import com.ctu.bookstore.entity.display.Comment;
import com.ctu.bookstore.entity.display.Product;
import com.ctu.bookstore.entity.payment.UserOrder;
import com.ctu.bookstore.enums.OrderStatus;
import com.ctu.bookstore.mapper.display.CommentMapper;
import com.ctu.bookstore.repository.identity.UserRepository;
import com.ctu.bookstore.repository.display.CommentRepository;
import com.ctu.bookstore.repository.display.ProductRepository;
import com.ctu.bookstore.repository.payment.UserOrderRepository;
import com.ctu.bookstore.service.identity.UserService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepository commentRepository;
    ProductRepository productRepository;
    UserRepository userRepository;
    CommentMapper commentMapper;
    UserService userService;

    @Transactional
    public CommentResponseDTO createComment(CommentRequestDTO request) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(name)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Tạo comment
        Comment comment = Comment.builder()
                .productId(request.getProductId())
                .comment(request.getComment())
                .rating(request.getRating())
                .createdAt(Instant.now())
                .userId(user.getId())
                .username(user.getUsername())
                .build();

        // Lưu comment trước để có id
        Comment savedComment = commentRepository.save(comment);

        // Đảm bảo list không null (phòng khi dữ liệu DB cũ)
//        if (product.getCommentId() == null) {
//            product.setCommentId(new ArrayList<>());
//        }
//        System.out.println("savedComment.getId() trong comment service: "+savedComment.getId());
//        product.getCommentId().add(savedComment.getId());
//        var prod = productRepository.save(product); // lưu lại product
//        System.out.println("product commentid trong comment service "+ prod.getCommentId());
        // ⭐ Cập nhật rating cho Product
        Integer oldCount = product.getRatingCount() == null ? 0 : product.getRatingCount();
        Double oldAvg   = product.getAverageStars() == null ? 0.0 : product.getAverageStars();

        int newCount = oldCount + 1;
        double newAvg = ((oldAvg * oldCount) + request.getRating()) / newCount;

        product.setRatingCount(newCount);
        product.setAverageStars(newAvg);

        productRepository.save(product);
        return commentMapper.toCommentResponse(savedComment);
    }

    public List<Comment> getCommentOfProduct(String productId){
        return commentRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    public Set<Product> getAllProductAllowComment(){
        Set<Product> products = new HashSet<>();

        Set<UserOrder> orders = userService.getAllOrders();
        if (orders == null || orders.isEmpty()) {
            return products; // trả list rỗng
        }

        orders.forEach(userOrder -> {
            if(userOrder.getStatus() == OrderStatus.PAID) {
                if (userOrder.getOrderItems() != null) {
                    userOrder.getOrderItems().forEach(item -> {
                        if (item.getProduct() != null) {
                            products.add(item.getProduct());
                        }
                    });
                }
            }
        });

        return products;
    }
}