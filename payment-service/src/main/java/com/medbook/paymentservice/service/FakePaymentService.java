package com.medbook.paymentservice.service;

import com.medbook.paymentservice.model.Payment;
import com.medbook.paymentservice.model.PaymentMethod;
import com.medbook.paymentservice.model.PaymentStatus;
import com.medbook.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FakePaymentService {

    private final PaymentRepository paymentRepository;

    // Tạo payment và trả về URL giả lập thanh toán
    public String createFakePayment(Long appointmentId, Long patientId, Long amount) {

        String transactionCode = "FAKE-" + UUID.randomUUID();

        Payment payment = new Payment();
        payment.setAppointmentId(appointmentId);
        payment.setPatientId(patientId);
        payment.setAmount((double) amount);
        payment.setMethod(PaymentMethod.FAKE);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionCode(transactionCode);
        payment.setTransactionTime(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        // Trả về URL redirect tới "FAKE PAYMENT UI"
        return "http://localhost:5173/fake-gateway?paymentId=" + saved.getId();
    }

    // Xử lý kết quả: success / fail
    public Payment handleReturn(Long paymentId, boolean success) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (success) {
            payment.setStatus(PaymentStatus.COMPLETED);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        paymentRepository.save(payment);
        return payment;
    }
}
