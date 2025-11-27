package com.medbook.appointmentservice.controller;

import com.medbook.appointmentservice.dto.PatientResponse;
import com.medbook.appointmentservice.model.Appointment;
import com.medbook.appointmentservice.service.AppointmentService;
import com.medbook.appointmentservice.dto.AppointmentResponse;
import com.medbook.appointmentservice.service.QRService;
import com.medbook.appointmentservice.dto.RatingRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;
    private final QRService qrService;

    // ========================= USER: GET MY APPOINTMENTS =========================
    @GetMapping("/my")
    public ResponseEntity<List<Appointment>> getMyAppointments(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(service.getAppointmentsByPatientEmail(email));
    }

    // ========================= AVAILABLE SLOTS =========================
    @GetMapping("/slots")
    public ResponseEntity<?> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam String date,
            @RequestParam Integer duration
    ) {
        return ResponseEntity.ok(service.getAvailableSlots(doctorId, date, duration));
    }

    // ========================= CREATE APPOINTMENT =========================
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(
            @RequestBody Appointment appointment,
            Authentication authentication
    ) {
        String email = authentication.getName();
        appointment.setPatientEmail(email);

        // ⭐ AUTO-FILL patientId via patient-service
        try {
            String url = "http://localhost:8080/api/patients/email/" + email;
            RestTemplate rest = new RestTemplate();

            PatientResponse p = rest.getForObject(url, PatientResponse.class);

            if (p != null && p.getId() != null) {
                appointment.setPatientId(p.getId().intValue());
                System.out.println(">>> AUTO-SET patientId = " + p.getId());
            } else {
                System.out.println("⚠ PatientService: Không tìm thấy ID cho email = " + email);
            }

        } catch (Exception e) {
            System.out.println("❌ Lỗi auto-fill patientId: " + e.getMessage());
        }

        Appointment created = service.createAppointment(appointment);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ========================= CRUD =========================
    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(service.getAllAppointments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAppointmentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(
            @PathVariable Long id,
            @RequestBody Appointment updated
    ) {
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

    // ========================= DOCTOR: GET PATIENT LIST =========================
    @GetMapping("/doctor/{doctorId}/patients")
    public ResponseEntity<List<Map<String, Object>>> getPatientsByDoctor(
            @PathVariable Integer doctorId
    ) {
        return ResponseEntity.ok(service.getPatientsByDoctorId(doctorId));
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

    // ========================= PAYMENT SUCCESS =========================
    @PutMapping("/{id}/paid")
    public ResponseEntity<String> markAppointmentAsPaid(@PathVariable Long id) {
        service.markAsPaid(id);
        return ResponseEntity.ok("Appointment " + id + " marked as PAID");
    }

    // ========================= PAYMENT FAIL =========================
    @PutMapping("/{id}/fail")
    public ResponseEntity<String> markPaymentFailed(@PathVariable Long id) {
        Appointment appt = service.getAppointmentById(id);

        appt.setPaymentStatus("FAILED");
        appt.setPaid(false);
        appt.setStatus("PENDING");

        service.updateAppointment(id, appt);

        return ResponseEntity.ok("Appointment " + id + " marked as FAILED");
    }

    // ========================= CANCEL UNPAID =========================
    @PutMapping("/{id}/cancel-unpaid")
    public ResponseEntity<String> autoCancelUnpaid(@PathVariable Long id) {
        Appointment appt = service.getAppointmentById(id);

        if ("PAID".equals(appt.getPaymentStatus())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Appointment already PAID, cannot auto-cancel.");
        }

        appt.setStatus("CANCELLED");
        appt.setPaymentStatus("UNPAID");
        appt.setPaid(false);

        service.updateAppointment(id, appt);

        return ResponseEntity.ok("Unpaid appointment " + id + " has been cancelled.");
    }

    // ========================= QR CODE =========================
    @GetMapping("/{id}/qr")
    public ResponseEntity<byte[]> getAppointmentQR(@PathVariable Long id) {

        Appointment appt = service.getAppointmentById(id);

        if (!"PAID".equals(appt.getPaymentStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String qrText = "APPT:" + appt.getId()
                + "|DATE:" + appt.getAppointmentDate()
                + "|TIME:" + appt.getAppointmentTime()
                + "|DOCTOR:" + appt.getDoctorId()
                + "|PATIENT_EMAIL:" + appt.getPatientEmail();

        byte[] qrImage = qrService.generateQRCode(qrText);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrImage);
    }

    // ========================= CONFIRM APPOINTMENT =========================
    @PutMapping("/{id}/confirm")
    public ResponseEntity<String> confirmAppointment(@PathVariable Long id) {
        service.confirm(id);
        return ResponseEntity.ok("Appointment " + id + " has been CONFIRMED");
    }

    // ========================= MARK COMPLETED =========================
    @PutMapping("/{id}/complete")
    public ResponseEntity<String> markCompleted(@PathVariable Long id) {
        service.markCompleted(id);
        return ResponseEntity.ok("Appointment " + id + " marked as COMPLETED");
    }

    // ========================= RATE DOCTOR =========================
    @PostMapping("/{id}/rating")
    public ResponseEntity<?> rateAppointment(
            @PathVariable Long id,
            @RequestBody RatingRequest req,
            Authentication auth
    ) {
        String email = auth.getName();

        try {
            Appointment updated = service.rateAppointment(id, req, email);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ========================= ⭐ RETURN REVIEWS DTO (FIXED) ⭐ =========================
    @GetMapping("/doctor/{doctorId}/reviews")
    public ResponseEntity<List<AppointmentResponse>> getDoctorReviews(@PathVariable Integer doctorId) {
        return ResponseEntity.ok(service.getDoctorReviewsDTO(doctorId));
    }

    // ========================= ⭐ MIGRATION: FIX MISSING patientId ⭐ =========================
    @PostMapping("/fix-missing-patient-id")
    public ResponseEntity<?> fixMissingPatientId() {

        List<Appointment> list = service.getAppointmentsMissingPatientId();
        RestTemplate rest = new RestTemplate();

        int fixed = 0;

        for (Appointment a : list) {
            if (a.getPatientEmail() == null) continue;

            try {
                String url = "http://localhost:8080/api/patients/email/" + a.getPatientEmail();
                PatientResponse p = rest.getForObject(url, PatientResponse.class);

                if (p != null && p.getId() != null) {
                    a.setPatientId(p.getId().intValue());
                    service.updateAppointment(a.getId(), a);
                    fixed++;
                }

            } catch (Exception ignored) {}
        }

        return ResponseEntity.ok("Fixed patient_id for " + fixed + " appointments");
    }
}
