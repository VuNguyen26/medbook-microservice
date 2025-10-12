package com.medbook.notificationservice.service;

import com.medbook.notificationservice.model.Notification;
import com.medbook.notificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    // 🟢 Lấy tất cả thông báo (cho admin hoặc test)
    public List<Notification> getAllNotifications() {
        return repository.findAll();
    }

    // 🟢 Lấy danh sách thông báo của 1 user
    public List<Notification> getNotificationsByUser(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // 🟢 Tạo mới 1 thông báo
    public Notification createNotification(Notification notification) {
        return repository.save(notification);
    }

    // 🟢 Đánh dấu thông báo là đã đọc
    public Notification markAsRead(Long id) {
        Notification notif = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        notif.setIsRead(true);
        return repository.save(notif);
    }

    // 🔴 Xóa thông báo theo ID
    public void deleteNotification(Long id) {
        repository.deleteById(id);
    }
}
