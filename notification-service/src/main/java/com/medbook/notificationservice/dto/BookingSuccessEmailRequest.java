package com.medbook.notificationservice.dto;

import lombok.Data;

@Data
public class BookingSuccessEmailRequest {

    private String email; // Gmail của khách hàng
    private String patientName; // Tên khách hàng
    private String doctorName; // Tên bác sĩ
    private String appointmentDate; // yyyy-MM-dd
    private String appointmentTime; // HH:mm:ss
}
