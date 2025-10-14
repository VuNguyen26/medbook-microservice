package com.medbook.prescriptionservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long medicalRecordId;   // tham chiếu medicalrecord-service

    @Column(nullable = false)
    private Long patientId;         // tham chiếu patient-service

    @Column(nullable = false)
    private Long doctorId;          // tham chiếu doctor-service

    @Column(length = 50, nullable = false)
    private String status;          // DRAFT / ACTIVE / CANCELLED

    @Column(columnDefinition = "TEXT")
    private String notes;           // hướng dẫn tổng quát

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    // ✅ Tự động gán issuedAt khi insert
    @PrePersist
    public void prePersist() {
        if (issuedAt == null) {
            issuedAt = LocalDateTime.now();
        }
    }
}
