package com.medbook.prescriptionservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record PrescriptionRequest(
        @NotNull(message = "medicalRecordId is required")
        Long medicalRecordId,

        @NotNull(message = "patientId is required")
        Long patientId,

        @NotNull(message = "doctorId is required")
        Long doctorId,

        @NotBlank(message = "status cannot be blank")
        String status,

        String notes,

        LocalDateTime issuedAt
) {}
