package com.medbook.appointmentservice.dto;

import lombok.Data;

@Data
public class PatientResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String gender;
    private Integer age;
}
