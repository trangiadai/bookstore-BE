package com.ctu.bookstore.controller.conversation;


import com.ctu.bookstore.dto.request.chat.ConversationRequestDTO;
import com.ctu.bookstore.dto.respone.ApiResponeDTO;
import com.ctu.bookstore.dto.respone.chat.ConversationResponseDTO;
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
//    @Autowired refactor version 1
    ConversationService conversationService;

    @PostMapping("/create")
    ApiResponeDTO<ConversationResponseDTO> createConversation(@RequestBody @Valid ConversationRequestDTO request) {
        return ApiResponeDTO.<ConversationResponseDTO>builder()
                .result(conversationService.create(request))
                .build();
    }
    @PostMapping("/create-default")
    ApiResponeDTO<ConversationResponseDTO> createConversation() {
        log.info("-------------------");
        return ApiResponeDTO.<ConversationResponseDTO>builder()
                .result(conversationService.createDefault())
                .build();
    }

    @GetMapping
    ApiResponeDTO<List<ConversationResponseDTO>> myConversations() {
        return ApiResponeDTO.<List<ConversationResponseDTO>>builder()
                .result(conversationService.myConversations())
                .build();
    }
}
