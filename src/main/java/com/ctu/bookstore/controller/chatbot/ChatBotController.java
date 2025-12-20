package com.ctu.bookstore.controller.chatbot;

import com.ctu.bookstore.service.chatbot.ChatBotService;
//import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatBotController {
    @Autowired
    private ChatBotService chatBotService;

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> req) {
        String answer = chatBotService.ask(req.get("message"));
        return Map.of("answer", answer);
    }
}