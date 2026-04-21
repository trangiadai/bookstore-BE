package com.ctu.bookstore.service.chat;

import com.ctu.bookstore.entity.chat.WebSocketSession;
import com.ctu.bookstore.repository.conversation.WebSocketSessionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class WebSocketSessionService {
    WebSocketSessionRepository webSocketSessionRepository;

    public WebSocketSession create(WebSocketSession webSocketSession){
        return webSocketSessionRepository.save(webSocketSession);
    }
    public void deleteSession(String sessionId){
        webSocketSessionRepository.deleteBySocketSessionId(sessionId);

    }
}
