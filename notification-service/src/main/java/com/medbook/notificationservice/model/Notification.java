package com.medbook.notificationservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // đồng bộ kiểu với User ID ở các service khác

    private String title;
    private String message;

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false; // mặc định chưa đọc

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(); // thời điểm tạo

    @Enumerated(EnumType.STRING)
    private NotificationType type; // Enum thay vì String
}
