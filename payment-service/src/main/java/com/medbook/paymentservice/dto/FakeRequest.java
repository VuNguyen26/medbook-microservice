package com.medbook.paymentservice.dto;

import lombok.Data;

@Data
public class FakeRequest {
    private Long appointmentId;
    private Long patientId;
    private Long amount;
}
