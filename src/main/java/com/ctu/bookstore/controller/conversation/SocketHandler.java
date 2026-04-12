package com.ctu.bookstore.controller.conversation;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.ctu.bookstore.dto.request.identity.IntrospectRequestDTO;
import com.ctu.bookstore.entity.chat.WebSocketSession;
import com.ctu.bookstore.repository.identity.UserRepository;
import com.ctu.bookstore.service.identity.AuthenticationService;
import com.ctu.bookstore.service.chat.WebSocketSessionService;
import com.nimbusds.jose.JOSEException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocketHandler {
    SocketIOServer server;
    AuthenticationService authenticationService;
    WebSocketSessionService webSocketSessionService;
    UserRepository userRepository ;

    @OnConnect
    public void clientConnected(SocketIOClient client) throws ParseException, JOSEException {
        String conversationId = client.getHandshakeData().getSingleUrlParam("conversationId");
//        client.joinRoom(conversationId);
        //get token from request param
        String token = client.getHandshakeData().getSingleUrlParam("token");
        //verify
        var introspectResponse = authenticationService.introspect(new IntrospectRequestDTO(token));
        if(introspectResponse.isValid()){
            log.info("client connected: {}", client.getSessionId());
            var user = userRepository.findByUsername(introspectResponse.getUserName());
            //lưu session id xuống
            WebSocketSession webSocketSession = WebSocketSession.builder()
                    .socketSessionId(client.getSessionId().toString())
                    .userId(user.get().getId())
                    .createdAt(Instant.now())
                    .build();
            webSocketSession = webSocketSessionService.create(webSocketSession);
            log.info("WebSocketSession created with id: {}", webSocketSession.getId());
        } else {
            log.error("Authenticarion fail: {}", client.getSessionId());
            client.disconnect(); // không đúng thì disconnect
        }

    }
    @OnDisconnect
    public void clientDisonnected(SocketIOClient client){

        log.info("client disconnected: {}", client.getSessionId());
        webSocketSessionService.deleteSession(client.getSessionId().toString());
    }
    @PostConstruct
    public void startServer(){
        server.start();
        server.addListeners(this);
        log.info("Socket server started");

    }
    @PreDestroy
    public void stopServer(){
        server.stop();
        log.info("Socket server stopped");
    }
}
