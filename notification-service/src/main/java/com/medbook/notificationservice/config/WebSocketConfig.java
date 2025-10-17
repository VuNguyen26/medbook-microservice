package com.medbook.notificationservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Client ReactJS sẽ connect vào đây
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")   // Cho phép mọi nguồn
                .withSockJS();                   // Dự phòng nếu client không hỗ trợ websocket thuần
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Các topic mà client sẽ subscribe
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
