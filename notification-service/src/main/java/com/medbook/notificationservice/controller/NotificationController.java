package com.medbook.notificationservice.controller;

import com.medbook.notificationservice.dto.BookingSuccessEmailRequest;
import com.medbook.notificationservice.model.Notification;
import com.medbook.notificationservice.model.NotificationType;
import com.medbook.notificationservice.service.EmailService;
import com.medbook.notificationservice.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications") // Bỏ "/api" để khớp Gateway (StripPrefix=1)
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService service;
    private final EmailService emailService;

    public NotificationController(NotificationService service, EmailService emailService) {
        this.service = service;
        this.emailService = emailService;
    }

    // API public test (không cần JWT)
    @GetMapping("/public/test")
    public String publicTest() {
        return "Public API: accessible without JWT token";
    }

    // API secure test (cần JWT)
    @GetMapping("/secure/test")
    public String secureTest() {
        return "Secure API: accessed successfully with valid JWT token";
    }

    // Lấy toàn bộ thông báo (Admin hoặc test)
    @GetMapping
    public List<Notification> getAll() {
        return service.getAllNotifications();
    }

    // Lấy danh sách thông báo của 1 user
    @GetMapping("/user/{userId}")
    public List<Notification> getByUser(@PathVariable Long userId) {
        return service.getNotificationsByUser(userId);
    }

    // Tạo thông báo mới (manual create)
    @PostMapping
    public Notification create(@RequestBody Notification notification) {
        return service.createNotification(notification);
    }

    // Đánh dấu thông báo là đã đọc
    @PutMapping("/{id}/read")
    public Notification markAsRead(@PathVariable Long id) {
        return service.markAsRead(id);
    }

    // Xóa thông báo
    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        service.deleteNotification(id);
    }

    // ======================================================
    // GỬI THÔNG BÁO TỰ ĐỘNG CHO SERVICE KHÁC
    // ======================================================
    @PostMapping("/send")
    public Notification sendNotification(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam NotificationType type
    ) {
        return service.sendNotification(userId, title, message, type);
    }

    // ======================================================
    // GỬI EMAIL XÁC NHẬN ĐẶT LỊCH THÀNH CÔNG
    // ======================================================
    @PostMapping("/email/booking-success")
    public ResponseEntity<?> sendBookingSuccessEmail(@RequestBody BookingSuccessEmailRequest request) {
        try {
            emailService.sendBookingSuccessEmail(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Không gửi được email: " + e.getMessage());
        }
    }
}
