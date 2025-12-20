package com.ctu.bookstore.controller.conversation;


import com.ctu.bookstore.dto.request.chat.ConversationRequest;
import com.ctu.bookstore.dto.respone.ApiRespone;
import com.ctu.bookstore.dto.respone.chat.ConversationResponse;
import com.ctu.bookstore.service.chat.ConversationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/conversations")
public class ConversationController {
//    @Autowired refactor version 1
    ConversationService conversationService;

    @PostMapping("/create")
    ApiRespone<ConversationResponse> createConversation(@RequestBody @Valid ConversationRequest request) {
        return ApiRespone.<ConversationResponse>builder()
                .result(conversationService.create(request))
                .build();
    }
    @PostMapping("/create-default")
    ApiRespone<ConversationResponse> createConversation() {
        log.info("-------------------");
        return ApiRespone.<ConversationResponse>builder()
                .result(conversationService.createDefault())
                .build();
    }

    @GetMapping("/my-conversations")
    ApiRespone<List<ConversationResponse>> myConversations() {
        return ApiRespone.<List<ConversationResponse>>builder()
                .result(conversationService.myConversations())
                .build();
    }
}
