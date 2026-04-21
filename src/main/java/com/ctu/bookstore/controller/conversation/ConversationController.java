package com.ctu.bookstore.controller.conversation;


import com.ctu.bookstore.dto.request.chat.ConversationRequestDTO;
import com.ctu.bookstore.dto.response.ApiResponseDTO;
import com.ctu.bookstore.dto.response.chat.ConversationResponseDTO;
import com.ctu.bookstore.service.chat.ConversationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/conversations")
public class ConversationController {
    ConversationService conversationService;

    @PostMapping("/create")
    ApiResponseDTO<ConversationResponseDTO> createConversation(@RequestBody @Valid ConversationRequestDTO request) {
        return ApiResponseDTO.<ConversationResponseDTO>builder()
                .result(conversationService.create(request))
                .build();
    }

    @PostMapping("/create-default")
    ApiResponseDTO<ConversationResponseDTO> createConversation() {
        log.info("-------------------");
        return ApiResponseDTO.<ConversationResponseDTO>builder()
                .result(conversationService.createDefault())
                .build();
    }

    @GetMapping
    ApiResponseDTO<List<ConversationResponseDTO>> myConversations() {
        return ApiResponseDTO.<List<ConversationResponseDTO>>builder()
                .result(conversationService.myConversations())
                .build();
    }
}
