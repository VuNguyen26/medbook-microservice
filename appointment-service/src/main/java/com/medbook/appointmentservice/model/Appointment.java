package com.medbook.appointmentservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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
    private String patientEmail;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;

    private String notes;
    private String status;
    private String paymentStatus;
    private String appointmentType;
    private Double fee;
    private Boolean paid;
    private String reason;
    private Long clinicLocationId;

    // ========================= RATING FIELDS =========================
    private Integer rating;

    @Column(length = 1000)
    private String ratingComment;

    private Boolean rated = false;

    private LocalDateTime ratedAt;
}
