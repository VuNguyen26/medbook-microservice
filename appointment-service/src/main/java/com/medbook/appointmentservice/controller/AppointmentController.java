package com.medbook.appointmentservice.controller;

import com.medbook.appointmentservice.model.Appointment;
import com.medbook.appointmentservice.service.AppointmentService;
import com.medbook.appointmentservice.dto.AppointmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    // Lấy tất cả cuộc hẹn
    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(service.getAllAppointments());
    }

    // Lấy cuộc hẹn theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAppointmentById(id));
    }

    // Tạo mới cuộc hẹn
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        Appointment created = service.createAppointment(appointment);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Cập nhật cuộc hẹn
    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @RequestBody Appointment updated) {
        Appointment result = service.updateAppointment(id, updated);
        return ResponseEntity.ok(result);
    }

    // Xóa cuộc hẹn
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        service.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    // Lấy cuộc hẹn theo bác sĩ
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getByDoctor(@PathVariable Integer doctorId) {
        return ResponseEntity.ok(service.getAppointmentsByDoctor(doctorId));
    }

    // Lấy cuộc hẹn theo bệnh nhân
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getByPatient(@PathVariable Integer patientId) {
        return ResponseEntity.ok(service.getAppointmentsByPatient(patientId));
    }

    // NEW: Lấy cuộc hẹn kèm thông tin bệnh nhân (gọi sang patient-service)
    @GetMapping("/{id}/with-patient")
    public ResponseEntity<AppointmentResponse> getAppointmentWithPatient(@PathVariable Long id) {
        AppointmentResponse response = service.getAppointmentWithPatient(id);
        return ResponseEntity.ok(response);
    }

    // NEW: Lấy cuộc hẹn kèm thông tin bác sĩ (gọi sang doctor-service)
    @GetMapping("/{id}/with-doctor")
    public ResponseEntity<AppointmentResponse> getAppointmentWithDoctor(@PathVariable Long id) {
        AppointmentResponse response = service.getAppointmentWithDoctor(id);
        return ResponseEntity.ok(response);
    }

    // NEW: Lấy đầy đủ thông tin (appointment + doctor + patient)
    @GetMapping("/{id}/with-full-info")
    public ResponseEntity<AppointmentResponse> getAppointmentWithFullInfo(@PathVariable Long id) {
        AppointmentResponse response = service.getAppointmentWithFullInfo(id);
        return ResponseEntity.ok(response);
    }

}
