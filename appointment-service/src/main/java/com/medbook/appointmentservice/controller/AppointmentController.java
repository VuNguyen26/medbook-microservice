package com.medbook.appointmentservice.controller;

import com.medbook.appointmentservice.model.Appointment;
import com.medbook.appointmentservice.service.AppointmentService;
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

    // Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAppointmentById(id));
    }

    // Tạo mới
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        Appointment created = service.createAppointment(appointment);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @RequestBody Appointment updated) {
        Appointment result = service.updateAppointment(id, updated);
        return ResponseEntity.ok(result);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        service.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    // Lấy theo bác sĩ
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getByDoctor(@PathVariable Integer doctorId) {
        return ResponseEntity.ok(service.getAppointmentsByDoctor(doctorId));
    }

    // Lấy theo bệnh nhân
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getByPatient(@PathVariable Integer patientId) {
        return ResponseEntity.ok(service.getAppointmentsByPatient(patientId));
    }
}
