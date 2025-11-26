package com.medbook.paymentservice.controller;

import com.medbook.paymentservice.dto.FakeRequest;
import com.medbook.paymentservice.dto.FakeResponse;
import com.medbook.paymentservice.model.Payment;
import com.medbook.paymentservice.service.FakePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments/fake")
@RequiredArgsConstructor
public class FakePaymentController {

    private final FakePaymentService fakePaymentService;

    // 1) Tạo thanh toán giả
    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody FakeRequest request) {

        String payUrl = fakePaymentService.createFakePayment(
                request.getAppointmentId(),
                request.getPatientId(),
                request.getAmount()
        );

        return ResponseEntity.ok(new FakeResponse(payUrl));
    }

    // 2) Xử lý callback trả về
    @GetMapping("/return")
    public ResponseEntity<?> handleReturn(
            @RequestParam Long paymentId,
            @RequestParam boolean success
    ) {
        Payment payment = fakePaymentService.handleReturn(paymentId, success);
        return ResponseEntity.ok(payment);
    }
}
