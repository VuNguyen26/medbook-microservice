package com.medbook.paymentservice.controller;

import com.medbook.paymentservice.model.Payment;
import com.medbook.paymentservice.repository.PaymentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments") // ✅ chỉ /payments (Gateway đã có /api/payments)
public class PaymentController {

    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // 🔹 Lấy tất cả payment
    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // 🔹 Tạo mới payment
    @PostMapping
    public Payment createPayment(@RequestBody Payment payment) {
        return paymentRepository.save(payment);
    }

    // 🔹 Lấy chi tiết payment theo ID
    @GetMapping("/{id}")
    public Payment getPaymentById(@PathVariable Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    // 🔹 Cập nhật payment (ví dụ khi thanh toán xong)
    @PutMapping("/{id}")
    public Payment updatePayment(@PathVariable Long id, @RequestBody Payment updatedPayment) {
        return paymentRepository.findById(id)
                .map(payment -> {
                    payment.setAmount(updatedPayment.getAmount());
                    payment.setMethod(updatedPayment.getMethod());
                    payment.setStatus(updatedPayment.getStatus());
                    payment.setTransactionTime(updatedPayment.getTransactionTime());
                    return paymentRepository.save(payment);
                })
                .orElse(null);
    }

    // 🔹 Xóa payment
    @DeleteMapping("/{id}")
    public void deletePayment(@PathVariable Long id) {
        paymentRepository.deleteById(id);
    }
}
