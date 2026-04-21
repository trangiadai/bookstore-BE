package com.ctu.bookstore.controller.conversation;

import com.ctu.bookstore.dto.request.chat.ChatMessageRequestDTO;
import com.ctu.bookstore.dto.response.ApiResponseDTO;
import com.ctu.bookstore.dto.response.chat.ChatMessageResponseDTO;
import com.ctu.bookstore.service.chat.ChatMessageService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/messages")
public class ChatMessageController {
    ChatMessageService chatMessageService;
    @PostMapping
    ApiResponseDTO<ChatMessageResponseDTO> create(@RequestBody @Valid ChatMessageRequestDTO request) {
        return ApiResponseDTO.<ChatMessageResponseDTO>builder()
                .result(chatMessageService.create(request))
                .build();
    }

    @GetMapping
    ApiResponseDTO<List<ChatMessageResponseDTO>> getMessages(@RequestParam("conversationId") String conversationId) {
        return ApiResponseDTO.<List<ChatMessageResponseDTO>>builder()
                .result(chatMessageService.getMessages(conversationId))
                .build();
    }
}
