package com.medbook.notificationservice.repository;

import com.medbook.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 🟢 Lấy danh sách thông báo của user, sắp xếp mới nhất trước
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
