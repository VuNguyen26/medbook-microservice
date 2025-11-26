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
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/payments")   // đi qua gateway: /api/payments/**
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
            HttpServletRequest request
    ) {
        try {
            payment.setStatus(PaymentStatus.PENDING);
            payment.setTransactionTime(LocalDateTime.now());

            Payment savedPayment = paymentRepository.save(payment);

            // Gọi AppointmentService để cập nhật trạng thái paid
            String appointmentUrl =
                    "http://gateway-service:8080/api/appointments/" +
                            payment.getAppointmentId() + "/paid";

            String authHeader = request.getHeader("Authorization");

            HttpHeaders headers = new HttpHeaders();
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                headers.set("Authorization", authHeader);
            }

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            restTemplate.exchange(
                    appointmentUrl,
                    HttpMethod.PUT,
                    entity,
                    String.class
            );

            savedPayment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(savedPayment);

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
            @RequestBody Payment updatedPayment
    ) {
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
