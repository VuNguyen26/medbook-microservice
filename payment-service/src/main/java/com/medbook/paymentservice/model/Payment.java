package com.medbook.paymentservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long appointmentId;
    private Long patientId;
    private Double amount;

    // Ánh xạ với converter để xử lý chữ thường ↔ Enum
    @Convert(converter = PaymentMethodConverter.class)
    private PaymentMethod method;

    @Convert(converter = PaymentStatusConverter.class)
    private PaymentStatus status;

    private LocalDateTime transactionTime = LocalDateTime.now();
}
