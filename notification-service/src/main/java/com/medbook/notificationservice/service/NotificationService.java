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

    public List<Notification> getNotificationsByUser(Integer userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Notification createNotification(Notification notification) {
        return repository.save(notification);
    }

    public Notification markAsRead(Long id) {
        Notification notif = repository.findById(id).orElseThrow();
        notif.setIsRead(true);
        return repository.save(notif);
    }
}
