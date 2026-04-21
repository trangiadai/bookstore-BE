package com.ctu.bookstore.mapper.chat;

import com.ctu.bookstore.dto.request.chat.ConversationRequestDTO;
import com.ctu.bookstore.dto.response.chat.ConversationResponseDTO;
import com.ctu.bookstore.entity.chat.Conversation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
    Conversation toConversation(ConversationRequestDTO request);
    ConversationResponseDTO toConversationResponse(Conversation conversation);
    List<ConversationResponseDTO> toConversationResponseList(List<Conversation> request);

}
