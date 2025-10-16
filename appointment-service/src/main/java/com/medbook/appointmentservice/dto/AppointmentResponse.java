package com.medbook.appointmentservice.dto;

import com.medbook.appointmentservice.model.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {

    private Appointment appointment;
    private PatientResponse patient;
    private DoctorResponse doctor;

    // Constructor khi chỉ có Appointment + Patient
    public AppointmentResponse(Appointment appointment, PatientResponse patient) {
        this.appointment = appointment;
        this.patient = patient;
    }

    // Constructor khi chỉ có Appointment + Doctor
    public AppointmentResponse(Appointment appointment, DoctorResponse doctor) {
        this.appointment = appointment;
        this.doctor = doctor;
    }
}
