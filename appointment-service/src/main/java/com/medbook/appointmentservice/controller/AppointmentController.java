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

    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(service.getAllAppointments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAppointmentById(id));
    }

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        Appointment created = service.createAppointment(appointment);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @RequestBody Appointment updated) {
        Appointment result = service.updateAppointment(id, updated);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        service.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getByDoctor(@PathVariable Integer doctorId) {
        return ResponseEntity.ok(service.getAppointmentsByDoctor(doctorId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getByPatient(@PathVariable Integer patientId) {
        return ResponseEntity.ok(service.getAppointmentsByPatient(patientId));
    }

    @GetMapping("/{id}/with-patient")
    public ResponseEntity<AppointmentResponse> getAppointmentWithPatient(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAppointmentWithPatient(id));
    }

    @GetMapping("/{id}/with-doctor")
    public ResponseEntity<AppointmentResponse> getAppointmentWithDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAppointmentWithDoctor(id));
    }

    @GetMapping("/{id}/with-full-info")
    public ResponseEntity<AppointmentResponse> getAppointmentWithFullInfo(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAppointmentWithFullInfo(id));
    }

    // Cập nhật trạng thái “PAID” từ PaymentService
    @PutMapping("/{id}/paid")
    public ResponseEntity<String> markAppointmentAsPaid(@PathVariable Long id) {
        service.markAsPaid(id);
        return ResponseEntity.ok("Appointment " + id + " marked as PAID");
    }
}
