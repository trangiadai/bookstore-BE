package com.ctu.bookstore.repository.display;

import com.ctu.bookstore.entity.display.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment,String> {
    List<Comment> findByProductIdOrderByCreatedAtDesc(String productId);
//    boolean existsByProductIdAndUserIdAndOrderId(String productId, String userId, Long orderId);
}