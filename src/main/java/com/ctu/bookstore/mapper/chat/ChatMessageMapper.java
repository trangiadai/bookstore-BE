package com.ctu.bookstore.mapper.chat;

import com.ctu.bookstore.dto.request.chat.ChatMessageRequestDTO;
import com.ctu.bookstore.dto.response.chat.ChatMessageResponseDTO;
import com.ctu.bookstore.entity.chat.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ChatMessageMapper {
    ChatMessageResponseDTO toChatMessageRespone(ChatMessage chatMessage);
    ChatMessage toChatMessage(ChatMessageRequestDTO request);
    List<ChatMessageResponseDTO> toChatMessageResponeList(List<ChatMessage> list);
}
