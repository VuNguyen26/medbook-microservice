package com.medbook.prescriptionservice.dto;

import java.time.LocalDateTime;

public record PrescriptionResponse(
        Long id,
        Long medicalRecordId,
        Long patientId,
        Long doctorId,
        String status,
        String notes,
        LocalDateTime issuedAt
) {}
