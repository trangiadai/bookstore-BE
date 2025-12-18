package com.ctu.bookstore.controller.display;

import com.ctu.bookstore.dto.request.display.CommentRequest;
import com.ctu.bookstore.dto.respone.ApiRespone;
import com.ctu.bookstore.dto.respone.display.CommentResponse;
import com.ctu.bookstore.entity.display.Comment;
import com.ctu.bookstore.mapper.display.CommentMapper;
import com.ctu.bookstore.service.display.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private CommentService commentService;
    private CommentMapper commentMapper;


    @PostMapping("/{productId}")
    public ApiRespone<CommentResponse> createReview(
            @PathVariable String productId,
            @Valid @RequestBody CommentRequest request
    )
    {
        return ApiRespone.<CommentResponse>builder()
                .result(commentService.createComment(productId,request))
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