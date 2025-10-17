package com.medbook.paymentservice.controller;

import com.medbook.paymentservice.model.*;
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
@RequestMapping("/payments") // gateway đã prefix /api/payments
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    // 🔹 Lấy tất cả payment
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentRepository.findAll());
    }

    // 🔹 Tạo mới payment
    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody Payment payment, HttpServletRequest request) {
        try {
            // Tạo payment mới
            payment.setTransactionTime(LocalDateTime.now());
            payment.setStatus(PaymentStatus.COMPLETED); // coi như thanh toán thành công

            Payment savedPayment = paymentRepository.save(payment);

            // 🔗 Gọi AppointmentService (qua Gateway) để cập nhật trạng thái “PAID”
            String appointmentUrl = "http://localhost:8080/api/appointments/" + payment.getAppointmentId() + "/paid";

            try {
                // Lấy JWT từ header request gốc
                String authHeader = request.getHeader("Authorization");

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", authHeader);
                HttpEntity<String> entity = new HttpEntity<>(headers);

                // Gọi sang appointment-service (PUT /paid) có kèm JWT
                restTemplate.exchange(appointmentUrl, HttpMethod.PUT, entity, String.class);

                System.out.println("Appointment #" + payment.getAppointmentId() + " đã được cập nhật thành PAID");
            } catch (Exception e) {
                System.err.println("Không thể cập nhật Appointment: " + e.getMessage());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(savedPayment);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tạo payment: " + e.getMessage());
        }
    }

    // 🔹 Lấy payment theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        Optional<Payment> payment = paymentRepository.findById(id);
        return payment.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy payment id=" + id));
    }

    // 🔹 Cập nhật payment
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable Long id, @RequestBody Payment updatedPayment) {
        Optional<Payment> existing = paymentRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy payment id=" + id);
        }

        Payment payment = existing.get();
        payment.setAmount(updatedPayment.getAmount());
        payment.setMethod(updatedPayment.getMethod());
        payment.setStatus(updatedPayment.getStatus());
        payment.setTransactionTime(LocalDateTime.now());
        paymentRepository.save(payment);

        return ResponseEntity.ok(payment);
    }

    // 🔹 Xóa payment
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Long id) {
        if (!paymentRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy payment id=" + id);
        }
        paymentRepository.deleteById(id);
        return ResponseEntity.ok("Đã xóa payment id=" + id);
    }
}
