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

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    // 🔹 Lấy tất cả thông báo (dành cho admin / test)
    public List<Notification> getAllNotifications() {
        return repository.findAll();
    }

    // 🔹 Lấy thông báo của một user
    public List<Notification> getNotificationsByUser(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // 🔹 Tạo thông báo mới (cho phép gọi từ các service khác)
    public Notification createNotification(Notification notification) {
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }
        if (notification.getType() == null) {
            notification.setType(NotificationType.SYSTEM);
        }
        return repository.save(notification);
    }

    // 🔹 Gửi thông báo tiện dụng (thường dùng khi Appointment/Payment gọi REST)
    public Notification sendNotification(Long userId, String title, String message, NotificationType type) {
        Notification notif = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        return repository.save(notif);
    }

    // 🔹 Đánh dấu đã đọc
    public Notification markAsRead(Long id) {
        Notification notif = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        notif.setIsRead(true);
        return repository.save(notif);
    }

    // 🔹 Xóa thông báo
    public void deleteNotification(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Notification not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
