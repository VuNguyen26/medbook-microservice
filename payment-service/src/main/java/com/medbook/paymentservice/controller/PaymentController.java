package com.medbook.paymentservice.controller;

import com.medbook.paymentservice.model.Payment;
import com.medbook.paymentservice.model.PaymentStatus;
import com.medbook.paymentservice.repository.PaymentRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/payments") // đi qua gateway: /api/payments/**
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    // =====================================================
    // GET ALL PAYMENTS
    // =====================================================
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentRepository.findAll());
    }

    // =====================================================
    // MOCK PAYMENT (nếu không dùng VNPay hoặc MoMo)
    // =====================================================
    @PostMapping
    public ResponseEntity<?> createPayment(
            @RequestBody Payment payment,
            HttpServletRequest request) {
        try {
            payment.setStatus(PaymentStatus.PENDING);
            payment.setTransactionTime(LocalDateTime.now());

            Payment savedPayment = paymentRepository.save(payment);

            // Gọi AppointmentService để cập nhật trạng thái paid
            String appointmentPaidUrl = "http://gateway-service:8080/api/appointments/" +
                    payment.getAppointmentId() + "/paid";

            String authHeader = request.getHeader("Authorization");

            HttpHeaders headers = new HttpHeaders();
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                headers.set("Authorization", authHeader);
            }

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            restTemplate.exchange(
                    appointmentPaidUrl,
                    HttpMethod.PUT,
                    entity,
                    String.class);

            savedPayment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(savedPayment);

            // =========================
            // Gửi email xác nhận đặt lịch thành công
            // =========================
            try {
                // Lấy thông tin lịch hẹn đầy đủ (bao gồm patientName, doctorName, patientEmail)
                String apptInfoUrl = "http://gateway-service:8080/api/appointments/" +
                        payment.getAppointmentId() + "/with-full-info";

                ResponseEntity<Map> apptRes = restTemplate.exchange(
                        apptInfoUrl,
                        HttpMethod.GET,
                        entity,
                        Map.class);

                Map<?, ?> appt = apptRes.getBody();

                if (appt != null) {
                    Object email = appt.get("patientEmail");
                    Object patientName = appt.get("patientName");
                    Object doctorName = appt.get("doctorName");
                    Object appointmentDate = appt.get("appointmentDate");
                    Object appointmentTime = appt.get("appointmentTime");

                    String notifyUrl = "http://gateway-service:8080/api/notifications/email/booking-success";

                    // Dùng HashMap để cho phép giá trị null (Map.of không cho phép null)
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("email", email);
                    payload.put("patientName", patientName);
                    payload.put("doctorName", doctorName);
                    payload.put("appointmentDate", appointmentDate);
                    payload.put("appointmentTime", appointmentTime);

                    HttpEntity<Map<String, Object>> emailEntity = new HttpEntity<>(payload, headers);
                    restTemplate.exchange(
                            notifyUrl,
                            HttpMethod.POST,
                            emailEntity,
                            Void.class);
                }
            } catch (Exception ex) {
                // Không làm fail payment nếu gửi email lỗi
                System.out.println("⚠ Không thể gửi email đặt lịch thành công: " + ex.getMessage());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(savedPayment);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi xử lý payment!");
        }
    }

    // =====================================================
    // GET PAYMENT BY ID
    // =====================================================
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        Optional<Payment> payment = paymentRepository.findById(id);
        return payment.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy payment id = " + id));
    }

    // =====================================================
    // UPDATE PAYMENT
    // =====================================================
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayment(
            @PathVariable Long id,
            @RequestBody Payment updatedPayment) {
        Optional<Payment> existing = paymentRepository.findById(id);

        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy payment id = " + id);
        }

        Payment p = existing.get();
        p.setAmount(updatedPayment.getAmount());
        p.setMethod(updatedPayment.getMethod());
        p.setStatus(updatedPayment.getStatus());
        p.setTransactionTime(LocalDateTime.now());

        paymentRepository.save(p);
        return ResponseEntity.ok(p);
    }

    // =====================================================
    // MARK PAYMENTS COMPLETED BY APPOINTMENT ID
    // - Dùng khi bác sĩ đánh dấu hoàn thành buổi khám
    // =====================================================
    @PutMapping("/by-appointment/{appointmentId}/complete")
    public ResponseEntity<?> markCompletedByAppointmentId(@PathVariable Long appointmentId) {
        List<Payment> payments = paymentRepository.findByAppointmentId(appointmentId);

        if (payments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy payment cho appointmentId = " + appointmentId);
        }

        LocalDateTime now = LocalDateTime.now();
        for (Payment p : payments) {
            p.setStatus(PaymentStatus.COMPLETED);
            p.setTransactionTime(now);
        }

        paymentRepository.saveAll(payments);
        return ResponseEntity.ok(payments);
    }

    // =====================================================
    // UPDATE PAYMENT STATUS ONLY (ADMIN MANUAL OVERRIDE)
    // =====================================================
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updatePaymentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Optional<Payment> existing = paymentRepository.findById(id);

        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy payment id = " + id);
        }

        String statusValue = body.get("status");
        if (statusValue == null || statusValue.isBlank()) {
            return ResponseEntity.badRequest().body("Thiếu trường 'status'");
        }

        PaymentStatus newStatus;
        try {
            newStatus = PaymentStatus.fromValue(statusValue);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Giá trị status không hợp lệ: " + statusValue);
        }

        Payment payment = existing.get();
        payment.setStatus(newStatus);
        payment.setTransactionTime(LocalDateTime.now());

        paymentRepository.save(payment);
        return ResponseEntity.ok(payment);
    }

    // =====================================================
    // DELETE PAYMENT
    // =====================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Long id) {
        if (!paymentRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy payment id = " + id);
        }

        paymentRepository.deleteById(id);
        return ResponseEntity.ok("Đã xóa payment id = " + id);
    }
}
