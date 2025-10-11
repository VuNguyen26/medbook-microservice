package com.medbook.prescriptionservice.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record PrescriptionRequest(
        @NotNull Long medicalRecordId,
        @NotNull Long patientId,
        @NotNull Long doctorId,
        @NotBlank String status,
        String notes,
        LocalDateTime issuedAt
) {}
