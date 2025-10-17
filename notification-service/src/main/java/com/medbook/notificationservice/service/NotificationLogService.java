package com.medbook.notificationservice.service;

import com.medbook.notificationservice.model.NotificationLog;
import com.medbook.notificationservice.repository.NotificationLogRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationLogService {

    private final NotificationLogRepository repository;

    public NotificationLogService(NotificationLogRepository repository) {
        this.repository = repository;
    }

    public void saveSendLog(Long userId, String type, boolean success, String errorMessage) {
        NotificationLog log = NotificationLog.builder()
                .userId(userId)
                .type(type)
                .success(success)
                .errorMessage(errorMessage)
                .build();
        repository.save(log);
    }
}
