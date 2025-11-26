package com.medbook.appointmentservice.service;

import com.medbook.appointmentservice.dto.AppointmentResponse;
import com.medbook.appointmentservice.dto.PatientResponse;
import com.medbook.appointmentservice.dto.DoctorResponse;
import com.medbook.appointmentservice.model.Appointment;
import com.medbook.appointmentservice.repository.AppointmentRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;
    private final RestTemplate restTemplate;

    // ========================= CRUD =========================
    public List<Appointment> getAllAppointments() {
        return repository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
    }

    // ========================= APPOINTMENT + PATIENT =========================
    public AppointmentResponse getAppointmentWithPatient(Long id) {
        Appointment appointment = getAppointmentById(id);

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "http://localhost:8080/api/patients/" + appointment.getPatientId();

        ResponseEntity<PatientResponse> res = restTemplate.exchange(
                url, HttpMethod.GET, entity, PatientResponse.class
        );

        AppointmentResponse output = new AppointmentResponse();
        output.setAppointment(appointment);
        output.setPatient(res.getBody());
        return output;
    }

    // ========================= APPOINTMENT + DOCTOR =========================
    public AppointmentResponse getAppointmentWithDoctor(Long id) {
        Appointment appointment = getAppointmentById(id);

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "http://localhost:8080/api/doctors/" + appointment.getDoctorId();

        ResponseEntity<DoctorResponse> res = restTemplate.exchange(
                url, HttpMethod.GET, entity, DoctorResponse.class
        );

        AppointmentResponse output = new AppointmentResponse();
        output.setAppointment(appointment);
        output.setDoctor(res.getBody());
        return output;
    }

    // ========================= FULL INFO =========================
    public AppointmentResponse getAppointmentWithFullInfo(Long id) {
        Appointment appointment = getAppointmentById(id);

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Doctor
        String doctorUrl = "http://localhost:8080/api/doctors/" + appointment.getDoctorId();
        DoctorResponse doctor = restTemplate.exchange(
                doctorUrl, HttpMethod.GET, entity, DoctorResponse.class
        ).getBody();

        // Patient
        String patientUrl = "http://localhost:8080/api/patients/" + appointment.getPatientId();
        PatientResponse patient = restTemplate.exchange(
                patientUrl, HttpMethod.GET, entity, PatientResponse.class
        ).getBody();

        AppointmentResponse output = new AppointmentResponse();
        output.setAppointment(appointment);
        output.setDoctor(doctor);
        output.setPatient(patient);
        return output;
    }

    // ========================= CREATE =========================
    public Appointment createAppointment(Appointment appointment) {
        if (appointment.getStatus() == null) appointment.setStatus("PENDING");
        if (appointment.getPaymentStatus() == null) appointment.setPaymentStatus("UNPAID");
        if (appointment.getPaid() == null) appointment.setPaid(false);
        return repository.save(appointment);
    }

    // ========================= UPDATE =========================
    public Appointment updateAppointment(Long id, Appointment updated) {
        Appointment existing = getAppointmentById(id);

        existing.setDoctorId(updated.getDoctorId());
        existing.setPatientId(updated.getPatientId());
        existing.setServiceId(updated.getServiceId());
        existing.setAppointmentDate(updated.getAppointmentDate());
        existing.setAppointmentTime(updated.getAppointmentTime());
        existing.setNotes(updated.getNotes());
        existing.setStatus(updated.getStatus());
        existing.setPaymentStatus(updated.getPaymentStatus());
        existing.setAppointmentType(updated.getAppointmentType());
        existing.setFee(updated.getFee());
        existing.setPaid(updated.getPaid());
        existing.setReason(updated.getReason());
        existing.setClinicLocationId(updated.getClinicLocationId());

        return repository.save(existing);
    }

    // ========================= DELETE =========================
    public void deleteAppointment(Long id) {
        repository.deleteById(id);
    }

    // ========================= FILTER =========================
    public List<Appointment> getAppointmentsByDoctor(Integer doctorId) {
        return repository.findByDoctorId(doctorId);
    }

    public List<Appointment> getAppointmentsByPatient(Integer patientId) {
        return repository.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsByPatientEmail(String email) {
        return repository.findByPatientEmail(email);
    }

    // ========================= PAYMENT =========================
    public Appointment markAsPaid(Long id) {
        Appointment a = getAppointmentById(id);
        a.setPaymentStatus("PAID");
        a.setPaid(true);
        return repository.save(a);
    }

    // ===================================================================
    // FULL FEATURE: TẠO KHUNG GIỜ TRỐNG (SÁNG – CHIỀU)
    // ===================================================================
    public List<AppointmentSlot> getAvailableSlots(Long doctorId, String date, int durationMinutes) {

        LocalDate targetDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Fix giờ làm việc
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(18, 0);

        Integer doctorIdInt = doctorId.intValue();

        List<Appointment> existingAppointments =
                repository.findByDoctorIdAndAppointmentDate(doctorIdInt, targetDate);

        List<AppointmentSlot> result = new ArrayList<>();

        LocalTime pointer = start;
        while (pointer.plusMinutes(durationMinutes).isBefore(end.plusMinutes(1))) {

            LocalTime slotStart = pointer;

            boolean isTaken = existingAppointments.stream().anyMatch(a ->
                    a.getAppointmentTime().equals(slotStart)
            );

            if (!isTaken) {
                result.add(new AppointmentSlot(
                        date + "T" + slotStart,
                        true
                ));
            }

            pointer = pointer.plusMinutes(30);
        }

        return result;
    }

    // ========================= SLOT DTO =========================
    public static class AppointmentSlot {
        public String start_at;
        public boolean available;

        public AppointmentSlot(String start_at, boolean available) {
            this.start_at = start_at;
            this.available = available;
        }
    }
}
