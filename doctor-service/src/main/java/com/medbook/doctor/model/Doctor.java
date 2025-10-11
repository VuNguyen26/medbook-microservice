package com.medbook.doctor.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "doctors")
@Data
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String specialty;
    private String phone;
    private String email;
}
