package com.medbook.appointmentservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {

    private Long id;

    private Integer patientId;
    private String patientEmail;

    private Integer doctorId;
    private Integer serviceId;

    private LocalDate appointmentDate;
    private LocalTime appointmentTime;

    private Integer rating;
    private String ratingComment;

    private String notes;
    private String status;
    private String paymentStatus;
    private Boolean paid;

    private String patientName;  // optional
    private String doctorName;   // optional
}
