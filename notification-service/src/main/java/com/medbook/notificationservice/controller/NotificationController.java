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

    @GetMapping("/user/{userId}")
    public List<Notification> getByUser(@PathVariable Integer userId) {
        return service.getNotificationsByUser(userId);
    }

    @PostMapping
    public Notification create(@RequestBody Notification notification) {
        return service.createNotification(notification);
    }

    @PutMapping("/{id}/read")
    public Notification markRead(@PathVariable Long id) {
        return service.markAsRead(id);
    }
}
