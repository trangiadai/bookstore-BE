package com.ctu.bookstore.service.chat;

import com.corundumstudio.socketio.SocketIOServer;
import com.ctu.bookstore.dto.request.chat.ChatMessageRequestDTO;
import com.ctu.bookstore.dto.respone.chat.ChatMessageResponeDTO;
import com.ctu.bookstore.entity.chat.ChatMessage;
import com.ctu.bookstore.entity.chat.ParticipantInfo;
import com.ctu.bookstore.entity.chat.WebSocketSession;
import com.ctu.bookstore.mapper.identity.UserMapper;
import com.ctu.bookstore.mapper.chat.ChatMessageMapper;
import com.ctu.bookstore.repository.identity.UserRepository;
import com.ctu.bookstore.repository.conversation.ChatMessageRepository;
import com.ctu.bookstore.repository.conversation.ConversationRepository;
import com.ctu.bookstore.repository.conversation.WebSocketSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatMessageService {

    @Autowired
    ConversationRepository conversationRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ChatMessageMapper chatMessageMapper;
    @Autowired
    ChatMessageRepository chatMessageRepository;
    @Autowired
    SocketIOServer socketIOServer;
    @Autowired
    WebSocketSessionRepository webSocketSessionRepository;

    public List<ChatMessageResponeDTO> getMessages(String conversationId) {
        var conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found with id: " + conversationId));

        // nếu bạn cần user hiện tại
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<ChatMessage> messages = chatMessageRepository.findAllByConversationIdOrderByCreatedDateDesc(conversation.getId());
        return messages.stream().map(this::toChatMessageRespone).toList();
    }
//    public ChatMessageRespone create(ChatMessageRequest chatMessageRequest) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        var user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found with username in ChatMessage sevice  : " + username));
//        var conversation = conversationRepository.findAllByParticipantIdsContains(user.getId()).stream().findFirst()
//                .orElseThrow(() -> new RuntimeException("Conversation not found for user in ChatMessage sevice : " + user.getId()));
//
//        // lọc người tham gia (nếu cần)
//        conversation.getParticipants().stream()
//                .filter(p -> user.getId().equals(p.getUserId()))
//                .findAny();
//
//        ChatMessage chatMessage = chatMessageMapper.toChatMessage(chatMessageRequest);
//        chatMessage.setSender(ParticipantInfo.builder()
//                .userId(user.getId())
//                .username(user.getUsername())
//                .avata(user.getAvatar())
//                .build());
//        chatMessage.setCreatedDate(Instant.now());
//
//        chatMessage = chatMessageRepository.save(chatMessage);
//        String message = chatMessage.getMessage();
//        //publish socket event to client
//        socketIOServer.getAllClients().forEach(client->{
//            client.sendEvent("message",message);
//        });
//
//        return toChatMessageRespone(chatMessage);
//    }
public ChatMessageResponeDTO create(ChatMessageRequestDTO chatMessageRequestDTO) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();

    var user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found with username : " + username));

    var conversation = conversationRepository.findAllByParticipantIdsContains(user.getId())
            .stream().findFirst()
            .orElseThrow(() -> new RuntimeException("Conversation not found for user : " + user.getId()));

    conversation.getParticipants().stream()
            .filter(participantInfo -> user.getId().equals(participantInfo.getUserId()))
            .findAny()
            .orElseThrow(()-> new RuntimeException("lỗi trong create chatmessage service"));
    ChatMessage chatMessage = chatMessageMapper.toChatMessage(chatMessageRequestDTO);
    chatMessage.setSender(ParticipantInfo.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .avata(user.getAvatar())
            .build());
    chatMessage.setCreatedDate(Instant.now());

    chatMessage = chatMessageRepository.save(chatMessage);
    String message = chatMessage.getMessage();

    // Tạo response object đúng chuẩn
    ChatMessageResponeDTO resp = toChatMessageRespone(chatMessage);
    System.out.println("======= BEFORE EMIT =======");
    System.out.println("SOCKET CLIENTS: " + socketIOServer.getAllClients().size());
    System.out.println("MESSAGE: " + message);
    // Emit realtime
    List<String> userIds = conversation.getParticipants().stream()
            .map(ParticipantInfo::getUserId).toList();
    Set<String> webSocketSessions =
            webSocketSessionRepository.findAllByUserIdIn(userIds)
                    .stream()
                    .map(WebSocketSession::getSocketSessionId)
                    .collect(Collectors.toSet());

    socketIOServer.getAllClients().forEach(client -> {
        if (webSocketSessions.contains(client.getSessionId().toString())){
            client.sendEvent("message", message);
        }

    });
    System.out.println("======= AFTER EMIT =======");
    return resp;
}


    //    private ChatMessageRespone toChatMessageRespone(ChatMessage chatMessage, User user) {
//        var chatMessageRespone = chatMessageMapper.toChatMessageRespone(chatMessage);
//        chatMessageRespone.setMe(user.getId().equals(chatMessageRespone.getSender().getUserId()));
//        return chatMessageRespone;
//    }
    private ChatMessageResponeDTO toChatMessageRespone(ChatMessage chatMessage){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(username);
        var chatMessageRespone = chatMessageMapper.toChatMessageRespone(chatMessage);
        chatMessageRespone.setMe(user.get().getId()
                .equals(chatMessageRespone.getSender().getUserId()));
        return chatMessageRespone; }
}
