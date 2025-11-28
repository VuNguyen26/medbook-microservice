package com.medbook.appointmentservice.service;

import com.medbook.appointmentservice.dto.AppointmentResponse;
import com.medbook.appointmentservice.dto.DoctorResponse;
import com.medbook.appointmentservice.dto.PatientResponse;
import com.medbook.appointmentservice.dto.RatingRequest;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;
    private final RestTemplate restTemplate;

    // ============================================================
    // 🔧 Helper: Lấy token hiện tại từ request
    // ============================================================
    private HttpEntity<String> getAuthEntity() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", request.getHeader("Authorization"));
        return new HttpEntity<>(headers);
    }

    // ============================================================
    // 🔧 Helper: Map Appointment -> AppointmentResponse FLAT DTO
    // ============================================================
    private AppointmentResponse mapToDto(Appointment a, PatientResponse p, DoctorResponse d) {
        AppointmentResponse dto = new AppointmentResponse();

        dto.setId(a.getId());
        dto.setPatientId(a.getPatientId());
        dto.setPatientEmail(a.getPatientEmail());
        dto.setDoctorId(a.getDoctorId());
        dto.setServiceId(a.getServiceId());
        dto.setAppointmentDate(a.getAppointmentDate());
        dto.setAppointmentTime(a.getAppointmentTime());
        dto.setRating(a.getRating());
        dto.setRatingComment(a.getRatingComment());
        dto.setNotes(a.getNotes());
        dto.setStatus(a.getStatus());
        dto.setPaymentStatus(a.getPaymentStatus());
        dto.setPaid(a.getPaid());

        if (p != null)
            dto.setPatientName(p.getFullName());
        if (d != null)
            dto.setDoctorName(d.getFullName());

        return dto;
    }

    // ========================= CRUD =========================
    public List<AppointmentResponse> getAllAppointments() {
        List<Appointment> list = repository.findAll();
        List<AppointmentResponse> result = new ArrayList<>();

        for (Appointment a : list) {
            PatientResponse p = null;
            DoctorResponse d = null;

            // Lấy thông tin bệnh nhân – nếu lỗi vẫn tiếp tục
            try {
                if (a.getPatientId() != null) {
                    p = restTemplate.exchange(
                            "http://gateway-service:8080/api/patients/" + a.getPatientId(),
                            HttpMethod.GET,
                            getAuthEntity(),
                            PatientResponse.class).getBody();
                }
            } catch (Exception ex) {
                System.out.println(
                        "⚠ Không lấy được thông tin patient cho appointment " + a.getId() + ": " + ex.getMessage());
            }

            // Lấy thông tin bác sĩ – nếu lỗi vẫn tiếp tục
            try {
                if (a.getDoctorId() != null) {
                    d = restTemplate.exchange(
                            "http://gateway-service:8080/api/doctors/" + a.getDoctorId(),
                            HttpMethod.GET,
                            getAuthEntity(),
                            DoctorResponse.class).getBody();
                }
            } catch (Exception ex) {
                System.out.println(
                        "⚠ Không lấy được thông tin doctor cho appointment " + a.getId() + ": " + ex.getMessage());
            }

            result.add(mapToDto(a, p, d));
        }

        return result;
    }

    public Appointment getAppointmentById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
    }

    // ========================= APPOINTMENT + PATIENT =========================
    public AppointmentResponse getAppointmentWithPatient(Long id) {
        Appointment appointment = getAppointmentById(id);

        PatientResponse p = restTemplate.exchange(
                "http://gateway-service:8080/api/patients/" + appointment.getPatientId(),
                HttpMethod.GET,
                getAuthEntity(),
                PatientResponse.class).getBody();

        return mapToDto(appointment, p, null);
    }

    // ========================= APPOINTMENT + DOCTOR =========================
    public AppointmentResponse getAppointmentWithDoctor(Long id) {
        Appointment appointment = getAppointmentById(id);

        DoctorResponse d = null;
        try {
            if (appointment.getDoctorId() != null) {
                d = restTemplate.exchange(
                        "http://gateway-service:8080/api/doctors/" + appointment.getDoctorId(),
                        HttpMethod.GET,
                        getAuthEntity(),
                        DoctorResponse.class).getBody();
            }
        } catch (Exception ex) {
            System.out.println("⚠ Không lấy được thông tin doctor cho appointment " + id + ": " + ex.getMessage());
        }

        return mapToDto(appointment, null, d);
    }

    // ========================= FULL INFO =========================
    public AppointmentResponse getAppointmentWithFullInfo(Long id) {
        Appointment a = getAppointmentById(id);

        PatientResponse p = null;
        DoctorResponse d = null;

        // Lấy thông tin bệnh nhân – nếu lỗi (403/404/...), vẫn trả về appointment
        try {
            if (a.getPatientId() != null) {
                p = restTemplate.exchange(
                        "http://gateway-service:8080/api/patients/" + a.getPatientId(),
                        HttpMethod.GET,
                        getAuthEntity(),
                        PatientResponse.class).getBody();
            }
        } catch (Exception ex) {
            System.out.println("⚠ Không lấy được thông tin patient cho appointment " + id + ": " + ex.getMessage());
        }

        // Lấy thông tin bác sĩ – nếu lỗi, vẫn tiếp tục
        try {
            if (a.getDoctorId() != null) {
                d = restTemplate.exchange(
                        "http://gateway-service:8080/api/doctors/" + a.getDoctorId(),
                        HttpMethod.GET,
                        getAuthEntity(),
                        DoctorResponse.class).getBody();
            }
        } catch (Exception ex) {
            System.out.println("⚠ Không lấy được thông tin doctor cho appointment " + id + ": " + ex.getMessage());
        }

        return mapToDto(a, p, d);
    }

    // ========================= CREATE =========================
    public Appointment createAppointment(Appointment appointment) {

        if (appointment.getStatus() == null)
            appointment.setStatus("PENDING");
        if (appointment.getPaymentStatus() == null)
            appointment.setPaymentStatus("UNPAID");
        if (appointment.getPaid() == null)
            appointment.setPaid(false);

        // Auto fill patient email nếu thiếu
        if (appointment.getPatientEmail() == null && appointment.getPatientId() != null) {
            try {
                PatientResponse p = restTemplate.getForObject(
                        "http://localhost:8080/api/patients/" + appointment.getPatientId(),
                        PatientResponse.class);

                if (p != null)
                    appointment.setPatientEmail(p.getEmail());

            } catch (Exception ignored) {
            }
        }

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

    // ========================= PATIENT LIST FOR DOCTOR =========================
    public List<Map<String, Object>> getPatientsByDoctorId(Integer doctorId) {
        return repository.findPatientStatsByDoctorId(doctorId);
    }

    // ========================= PAYMENT HANDLING =========================
    public Appointment markAsPaid(Long id) {
        Appointment a = getAppointmentById(id);
        a.setPaymentStatus("PAID");
        a.setPaid(true);
        return repository.save(a);
    }

    public Appointment confirm(Long id) {
        Appointment a = getAppointmentById(id);

        if (!"PAID".equals(a.getPaymentStatus()))
            throw new RuntimeException("Cannot confirm appointment before payment");

        a.setStatus("CONFIRMED");
        return repository.save(a);
    }

    public Appointment markCompleted(Long id) {
        Appointment a = getAppointmentById(id);

        if (!"PAID".equals(a.getPaymentStatus()))
            throw new RuntimeException("Cannot complete appointment before payment");

        a.setStatus("COMPLETED");
        return repository.save(a);
    }

    // ========================= RATING =========================
    public Appointment rateAppointment(Long id, RatingRequest req, String email) {

        Appointment a = getAppointmentById(id);

        if (!"COMPLETED".equals(a.getStatus()))
            throw new RuntimeException("Only completed appointments can be rated.");

        if (!a.getPatientEmail().equalsIgnoreCase(email))
            throw new RuntimeException("You are not allowed to rate this appointment.");

        if (Boolean.TRUE.equals(a.getRated()))
            throw new RuntimeException("This appointment is already rated.");

        if (req.getRating() == null || req.getRating() < 1 || req.getRating() > 5)
            throw new RuntimeException("Rating must be between 1 and 5.");

        a.setRated(true);
        a.setRating(req.getRating());
        a.setRatingComment(req.getComment());
        a.setRatedAt(LocalDateTime.now());

        return repository.save(a);
    }

    // ============================================================
    // ⭐⭐ DOCTOR REVIEWS (API FE ĐANG DÙNG) ⭐⭐
    // ============================================================
    public List<AppointmentResponse> getDoctorReviewsDTO(Integer doctorId) {

        List<Appointment> list = repository.findByDoctorIdAndRatedTrue(doctorId);
        List<AppointmentResponse> result = new ArrayList<>();

        for (Appointment a : list) {

            PatientResponse p = null;
            DoctorResponse d = null;

            try {
                p = restTemplate.exchange(
                        "http://localhost:8080/api/patients/" + a.getPatientId(),
                        HttpMethod.GET, getAuthEntity(), PatientResponse.class).getBody();
            } catch (Exception ignored) {
            }

            try {
                d = restTemplate.exchange(
                        "http://localhost:8080/api/doctors/" + a.getDoctorId(),
                        HttpMethod.GET, getAuthEntity(), DoctorResponse.class).getBody();
            } catch (Exception ignored) {
            }

            result.add(mapToDto(a, p, d));
        }

        return result;
    }

    // ===================================================================
    // SLOT GENERATION
    // ===================================================================
    public List<AppointmentSlot> getAvailableSlots(Long doctorId, String date, int durationMinutes) {

        LocalDate targetDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(18, 0);

        List<Appointment> existingAppointments = repository.findByDoctorIdAndAppointmentDate(doctorId.intValue(),
                targetDate);

        List<AppointmentSlot> result = new ArrayList<>();

        LocalTime pointer = start;
        while (pointer.plusMinutes(durationMinutes).isBefore(end.plusMinutes(1))) {

            LocalTime slotStart = pointer;

            boolean isTaken = existingAppointments.stream().anyMatch(a -> a.getAppointmentTime().equals(slotStart)
                    && "PAID".equals(a.getPaymentStatus()));

            if (!isTaken) {
                result.add(new AppointmentSlot(date + "T" + slotStart, true));
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

    public List<Appointment> getAppointmentsMissingPatientId() {
        return repository.findByPatientIdIsNull();
    }
}
