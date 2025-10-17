package com.medbook.notificationservice.repository;

import com.medbook.notificationservice.model.NotificationLog;
import com.medbook.notificationservice.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findByUserId(Long userId);
    List<NotificationLog> findByType(String type);
}
