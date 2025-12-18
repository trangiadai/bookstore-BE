package com.ctu.bookstore.configuration;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketIOConfig {
    @Bean
    public SocketIOServer socketIOServer(){
        com.corundumstudio.socketio.Configuration configuration = new com.corundumstudio.socketio.Configuration();
        configuration.setPort(8099);
        configuration.setHostname("localhost");  // ✅ BẮT BUỘC
//        configuration.setPort(8080);
        configuration.setOrigin("*"); // có thể connect từ bất cứ đâu


        return new SocketIOServer(configuration);
    }
}
