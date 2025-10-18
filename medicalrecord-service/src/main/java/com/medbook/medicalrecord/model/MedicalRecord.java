package com.medbook.medicalrecord.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;          // ID bệnh nhân
    private Long doctorId;           // ID bác sĩ
    private String diagnosis;        // Chẩn đoán

    @Column(length = 1000)
    private String notes;            // Ghi chú của bác sĩ

    @Builder.Default
    private LocalDateTime recordDate = LocalDateTime.now();   // Ngày tạo hồ sơ

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();    // Ngày cập nhật gần nhất

    // Có thể thêm phương thức cập nhật thời gian
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
