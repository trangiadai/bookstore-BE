package com.ctu.bookstore.mapper.chat;

import com.ctu.bookstore.dto.request.chat.ConversationRequest;
import com.ctu.bookstore.dto.respone.chat.ConversationResponse;
import com.ctu.bookstore.entity.chat.Conversation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
    Conversation toConversation(ConversationRequest request);
    ConversationResponse toConversationResponse(Conversation conversation);
    List<ConversationResponse> toConversationResponseList(List<Conversation> request);

}
