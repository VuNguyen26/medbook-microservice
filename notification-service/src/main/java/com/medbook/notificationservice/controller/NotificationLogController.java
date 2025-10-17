package com.medbook.notificationservice.controller;

import com.medbook.notificationservice.model.NotificationLog;
import com.medbook.notificationservice.model.NotificationType;
import com.medbook.notificationservice.repository.NotificationLogRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification-logs")
@CrossOrigin(origins = "*")
public class NotificationLogController {

    private final NotificationLogRepository repository;

    public NotificationLogController(NotificationLogRepository repository) {
        this.repository = repository;
    }

    // Lấy toàn bộ log
    @GetMapping
    public List<NotificationLog> getAllLogs() {
        return repository.findAll();
    }

    // Lấy log theo userId
    @GetMapping("/user/{userId}")
    public List<NotificationLog> getLogsByUser(@PathVariable Long userId) {
        return repository.findByUserId(userId);
    }

    // Lấy log theo loại thông báo (PAYMENT, APPOINTMENT, v.v.)
    @GetMapping("/type/{type}")
    public List<NotificationLog> getLogsByType(@PathVariable String type) {
        return repository.findByType(type.toUpperCase());
    }
}
