package com.ctu.bookstore.controller.conversation;

import com.ctu.bookstore.dto.request.chat.ChatMessageRequestDTO;
import com.ctu.bookstore.dto.respone.ApiResponeDTO;
import com.ctu.bookstore.dto.respone.chat.ChatMessageResponeDTO;
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
    ApiResponeDTO<ChatMessageResponeDTO> create(@RequestBody @Valid ChatMessageRequestDTO request) {
        return ApiResponeDTO.<ChatMessageResponeDTO>builder()
                .result(chatMessageService.create(request))
                .build();
    }

    @GetMapping
    ApiResponeDTO<List<ChatMessageResponeDTO>> getMessages(@RequestParam("conversationId") String conversationId) {
        return ApiResponeDTO.<List<ChatMessageResponeDTO>>builder()
                .result(chatMessageService.getMessages(conversationId))
                .build();
    }
}
