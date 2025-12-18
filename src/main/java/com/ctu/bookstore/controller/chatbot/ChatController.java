package com.ctu.bookstore.controller.chatbot;

import com.ctu.bookstore.dto.request.chatbot.ChatRequest;
import com.ctu.bookstore.service.chatbot.ChatService;
//import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatController {
    @Autowired
    private ChatService chatService;

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> req) {
        String answer = chatService.ask(req.get("message"));
        return Map.of("answer", answer);
    }
}