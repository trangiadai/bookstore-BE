package com.ctu.bookstore.mapper.chat;

import com.ctu.bookstore.dto.request.chat.ChatMessageRequestDTO;
import com.ctu.bookstore.dto.respone.chat.ChatMessageResponeDTO;
import com.ctu.bookstore.entity.chat.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ChatMessageMapper {
    ChatMessageResponeDTO toChatMessageRespone(ChatMessage chatMessage);
    ChatMessage toChatMessage(ChatMessageRequestDTO request);
    List<ChatMessageResponeDTO> toChatMessageResponeList(List<ChatMessage> list);
}
