package com.medbook.notificationservice.service;

import com.medbook.notificationservice.model.Notification;
import com.medbook.notificationservice.model.NotificationType;
import com.medbook.notificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationLogService logService;                // Ghi log lịch sử gửi
    private final WebSocketNotificationService webSocketService;    // Gửi realtime

    // Constructor inject đầy đủ 3 service
    public NotificationService(NotificationRepository repository,
                               NotificationLogService logService,
                               WebSocketNotificationService webSocketService) {
        this.repository = repository;
        this.logService = logService;
        this.webSocketService = webSocketService;
    }

    // Lấy tất cả thông báo (admin / test)
    public List<Notification> getAllNotifications() {
        return repository.findAll();
    }

    // Lấy thông báo của một user
    public List<Notification> getNotificationsByUser(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Tạo thông báo thủ công (gọi từ FE hoặc API)
    public Notification createNotification(Notification notification) {
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }
        if (notification.getType() == null) {
            notification.setType(NotificationType.SYSTEM);
        }
        Notification saved = repository.save(notification);

        // Ghi log gửi
        logService.saveSendLog(notification.getUserId(), notification.getType().name(), true, null);

        // Gửi realtime (nếu có WebSocket client)
        webSocketService.sendToClient(saved);

        return saved;
    }

    // Gửi thông báo tiện dụng (dùng khi Appointment/Payment gọi REST)
    public Notification sendNotification(Long userId, String title, String message, NotificationType type) {
        try {
            Notification notif = Notification.builder()
                    .userId(userId)
                    .title(title)
                    .message(message)
                    .type(type)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            Notification saved = repository.save(notif);

            // Ghi log thành công
            logService.saveSendLog(userId, type.name(), true, null);

            // Đẩy realtime
            webSocketService.sendToClient(saved);

            return saved;
        } catch (Exception e) {
            // Nếu có lỗi thì ghi lại log fail
            logService.saveSendLog(userId, type.name(), false, e.getMessage());
            throw e;
        }
    }

    // Đánh dấu đã đọc
    public Notification markAsRead(Long id) {
        Notification notif = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        notif.setIsRead(true);
        return repository.save(notif);
    }

    // Xóa thông báo
    public void deleteNotification(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Notification not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
