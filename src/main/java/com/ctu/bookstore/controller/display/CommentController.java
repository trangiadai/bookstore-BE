package com.ctu.bookstore.controller.display;

import com.ctu.bookstore.dto.request.display.CommentRequestDTO;
import com.ctu.bookstore.dto.response.ApiResponseDTO;
import com.ctu.bookstore.dto.response.display.CommentResponseDTO;
import com.ctu.bookstore.entity.display.Comment;
import com.ctu.bookstore.mapper.display.CommentMapper;
import com.ctu.bookstore.service.display.CommentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
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
    public ApiResponseDTO<CommentResponseDTO> createReview(@Valid @RequestBody CommentRequestDTO request)
    {
        return ApiResponseDTO.<CommentResponseDTO>builder()
                .result(commentService.createComment(request))
                .build();
    }

    @GetMapping("/{productId}")
    public ApiResponseDTO<List<CommentResponseDTO>> getCommentOfProduct(@PathVariable String productId) {
        List<Comment> comments = commentService.getCommentOfProduct(productId);
        List<CommentResponseDTO> responses = comments.stream()
                .map(commentMapper::toCommentResponse)
                .toList();
        return ApiResponseDTO.<List<CommentResponseDTO>>builder()
                .result(responses)
                .build();
    }

}