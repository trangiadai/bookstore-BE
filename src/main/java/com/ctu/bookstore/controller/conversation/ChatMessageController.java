package com.ctu.bookstore.controller.conversation;


import com.ctu.bookstore.dto.request.chat.ChatMessageRequest;
import com.ctu.bookstore.dto.respone.ApiRespone;
import com.ctu.bookstore.dto.respone.chat.ChatMessageRespone;
import com.ctu.bookstore.service.chat.ChatMessageService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("messages")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageController {
    ChatMessageService chatMessageService;
    @PostMapping("/create")
    ApiRespone<ChatMessageRespone> create(
            @RequestBody @Valid ChatMessageRequest request) {
        return ApiRespone.<ChatMessageRespone>builder()
                .result(chatMessageService.create(request))
                .build();
    }

    @GetMapping
    ApiRespone<List<ChatMessageRespone>> getMessages(
            @RequestParam("conversationId") String conversationId) {
        return ApiRespone.<List<ChatMessageRespone>>builder()
                .result(chatMessageService.getMessages(conversationId))
                .build();
    }
}
