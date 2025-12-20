package com.ctu.bookstore.controller.display;

import com.ctu.bookstore.dto.request.display.CommentRequest;
import com.ctu.bookstore.dto.respone.ApiRespone;
import com.ctu.bookstore.dto.respone.display.CommentResponse;
import com.ctu.bookstore.entity.display.Comment;
import com.ctu.bookstore.mapper.display.CommentMapper;
import com.ctu.bookstore.service.display.CommentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/comment")
public class CommentController {
    CommentService commentService;
    CommentMapper commentMapper;

    @PostMapping("/{productId}")
    public ApiRespone<CommentResponse> createReview(@Valid @RequestBody CommentRequest request)
    {
        return ApiRespone.<CommentResponse>builder()
                .result(commentService.createComment(request))
                .build();
    }

    @GetMapping("/{productId}")
    public ApiRespone<List<CommentResponse>> getCommentOfProduct(@PathVariable String productId) {
        List<Comment> comments = commentService.getCommentOfProduct(productId);
        List<CommentResponse> responses = comments.stream()
                .map(commentMapper::toCommentResponse)
                .toList();
        return ApiRespone.<List<CommentResponse>>builder()
                .result(responses)
                .build();
    }

}