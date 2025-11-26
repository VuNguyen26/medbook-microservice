package com.medbook.appointmentservice.controller;

import com.medbook.appointmentservice.model.Appointment;
import com.medbook.appointmentservice.service.AppointmentService;
import com.medbook.appointmentservice.dto.AppointmentResponse;
import com.medbook.appointmentservice.service.QRService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;
    private final QRService qrService;

    // ========================= NEW: GET APPOINTMENTS OF LOGGED-IN USER =========================
    @GetMapping("/my")
    public ResponseEntity<List<Appointment>> getMyAppointments(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(service.getAppointmentsByPatientEmail(email));
    }

    // ========================= AVAILABLE TIME SLOTS =========================
    @GetMapping("/slots")
    public ResponseEntity<?> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam String date,
            @RequestParam Integer duration
    ) {
        return ResponseEntity.ok(service.getAvailableSlots(doctorId, date, duration));
    }

    // ========================= BASIC CRUD =========================

    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(service.getAllAppointments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAppointmentById(id));
    }

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(
            @RequestBody Appointment appointment,
            Authentication authentication
    ) {
        // GẮN EMAIL USER VÀO APPOINTMENT
        String email = authentication.getName();
        appointment.setPatientEmail(email);

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

    // ========================= FILTER BY DOCTOR / PATIENT =========================

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getByDoctor(@PathVariable Integer doctorId) {
        return ResponseEntity.ok(service.getAppointmentsByDoctor(doctorId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getByPatient(@PathVariable Integer patientId) {
        return ResponseEntity.ok(service.getAppointmentsByPatient(patientId));
    }

    // ========================= EXTRA INFO =========================
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

    // ========================= PAYMENT UPDATE =========================
    @PutMapping("/{id}/paid")
    public ResponseEntity<String> markAppointmentAsPaid(@PathVariable Long id) {
        service.markAsPaid(id);
        return ResponseEntity.ok("Appointment " + id + " marked as PAID");
    }

    // ========================= QR CODE =========================
    @GetMapping("/{id}/qr")
    public ResponseEntity<String> getAppointmentQR(@PathVariable Long id) {

        Appointment appt = service.getAppointmentById(id);

        String qrText = "APPT:" + appt.getId()
                + "|DATE:" + appt.getAppointmentDate()
                + "|TIME:" + appt.getAppointmentTime()
                + "|DOCTOR:" + appt.getDoctorId()
                + "|PATIENT_EMAIL:" + appt.getPatientEmail();

        byte[] qrImage = qrService.generateQRCode(qrText);
        String base64 = Base64.getEncoder().encodeToString(qrImage);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(base64);
    }
}
