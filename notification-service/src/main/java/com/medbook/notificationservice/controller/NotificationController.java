package com.medbook.notificationservice.controller;

import com.medbook.notificationservice.model.Notification;
import com.medbook.notificationservice.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    // 🟢 Lấy toàn bộ thông báo (Admin hoặc test)
    @GetMapping
    public List<Notification> getAll() {
        return service.getAllNotifications();
    }

    // 🟢 Lấy danh sách thông báo của user cụ thể
    @GetMapping("/user/{userId}")
    public List<Notification> getByUser(@PathVariable Long userId) {
        return service.getNotificationsByUser(userId);
    }

    // 🟢 Tạo thông báo mới
    @PostMapping
    public Notification create(@RequestBody Notification notification) {
        return service.createNotification(notification);
    }

    // 🟢 Đánh dấu thông báo là đã đọc
    @PutMapping("/{id}/read")
    public Notification markAsRead(@PathVariable Long id) {
        return service.markAsRead(id);
    }

    // 🔴 Xóa thông báo (nếu cần)
    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        service.deleteNotification(id);
    }
}
