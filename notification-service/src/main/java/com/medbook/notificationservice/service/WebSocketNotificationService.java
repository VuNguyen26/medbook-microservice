package com.medbook.notificationservice.service;

import com.medbook.notificationservice.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Gửi thông báo realtime đến React client
     * Client có thể subscribe theo userId hoặc topic chung
     */
    public void sendToClient(Notification notification) {
        try {
            // Gửi đến tất cả client đang subscribe vào /topic/notifications
            messagingTemplate.convertAndSend("/topic/notifications", notification);

            // (Tuỳ chọn) gửi riêng từng user:
            // messagingTemplate.convertAndSend("/topic/notifications/" + notification.getUserId(), notification);

        } catch (Exception e) {
            System.err.println("Lỗi khi gửi WebSocket: " + e.getMessage());
        }
    }
}
