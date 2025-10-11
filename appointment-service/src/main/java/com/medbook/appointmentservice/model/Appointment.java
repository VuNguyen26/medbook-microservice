package com.medbook.appointmentservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer patientId;
    private Integer doctorId;
    private Integer serviceId;

    private LocalDateTime appointmentDate;
    private LocalTime appointmentTime;
    private String notes;
    private String status;
    private String paymentStatus;
    private String appointmentType;
    private Double fee;
    private Boolean paid;
    private String reason;
    private Long clinicLocationId;
}
