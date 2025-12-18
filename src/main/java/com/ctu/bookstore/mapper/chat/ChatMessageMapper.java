package com.ctu.bookstore.mapper.chat;

import com.ctu.bookstore.dto.request.chat.ChatMessageRequest;
import com.ctu.bookstore.dto.respone.chat.ChatMessageRespone;
import com.ctu.bookstore.entity.chat.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ChatMessageMapper {
    ChatMessageRespone toChatMessageRespone(ChatMessage chatMessage);
    ChatMessage toChatMessage(ChatMessageRequest request);
    List<ChatMessageRespone> toChatMessageResponeList(List<ChatMessage> list);
}
