package com.ctu.bookstore.repository.conversation;

import com.ctu.bookstore.entity.chat.WebSocketSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebSocketSessionRepository extends MongoRepository<WebSocketSession,String> {
    void deleteBySocketSessionId(String socketId);
    List<WebSocketSession> findAllByUserIdIn(List<String> userIds);
}
