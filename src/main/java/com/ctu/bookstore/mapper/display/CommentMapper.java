package com.ctu.bookstore.mapper.display;

import com.ctu.bookstore.dto.respone.display.CommentResponse;
import com.ctu.bookstore.entity.display.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring" ,uses = {ProductImageMapper.class})
public interface CommentMapper {
    CommentResponse toCommentResponse(Comment comment);
}