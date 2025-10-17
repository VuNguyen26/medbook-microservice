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

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;
    private final RestTemplate restTemplate; // Inject từ AppointmentServiceApplication

    // Lấy tất cả lịch hẹn
    public List<Appointment> getAllAppointments() {
        return repository.findAll();
    }

    // Lấy lịch hẹn theo ID (thuần)
    public Appointment getAppointmentById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
    }

    // Lấy lịch hẹn kèm thông tin bệnh nhân
    // Lấy lịch hẹn kèm thông tin bệnh nhân (có JWT)
    public AppointmentResponse getAppointmentWithPatient(Long id) {
        Appointment appointment = getAppointmentById(id);

        // Lấy token từ request hiện tại
        HttpServletRequest currentRequest =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String authHeader = currentRequest.getHeader("Authorization");
        System.out.println("[AppointmentService] Calling patient-service with token: " + authHeader);

        // Gắn token vào header khi gọi sang patient-service
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Gọi sang patient-service qua GATEWAY (đảm bảo đi qua cổng 8080)
        String url = "http://localhost:8080/api/patients/" + appointment.getPatientId();

        ResponseEntity<PatientResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                PatientResponse.class
        );

        PatientResponse patient = response.getBody();

        AppointmentResponse result = new AppointmentResponse();
        result.setAppointment(appointment);
        result.setPatient(patient);

        return result;
    }


    // Lấy lịch hẹn kèm thông tin bác sĩ (truyền JWT token)
    public AppointmentResponse getAppointmentWithDoctor(Long id) {
        Appointment appointment = getAppointmentById(id);

        // Gọi sang doctor-service (qua Gateway)
        String url = "http://localhost:8080/api/doctors/" + appointment.getDoctorId();

        // Lấy token từ request hiện tại
        HttpServletRequest currentRequest =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String authHeader = currentRequest.getHeader("Authorization");
        System.out.println("[AppointmentService] Calling doctor-service with token: " + authHeader);


        // Đính kèm Authorization header vào RestTemplate request
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<DoctorResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                DoctorResponse.class
        );

        DoctorResponse doctor = response.getBody();
        AppointmentResponse result = new AppointmentResponse();
        result.setAppointment(appointment);
        result.setDoctor(doctor);

        return result;
    }

    // Lấy lịch hẹn kèm đầy đủ thông tin bác sĩ và bệnh nhân
    public AppointmentResponse getAppointmentWithFullInfo(Long id) {
        Appointment appointment = getAppointmentById(id);

        // Lấy token từ request hiện tại
        HttpServletRequest currentRequest =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String authHeader = currentRequest.getHeader("Authorization");

        // Gắn token vào header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Gọi sang doctor-service
        String doctorUrl = "http://localhost:8080/api/doctors/" + appointment.getDoctorId();
        ResponseEntity<DoctorResponse> doctorResponse = restTemplate.exchange(
                doctorUrl,
                HttpMethod.GET,
                entity,
                DoctorResponse.class
        );
        DoctorResponse doctor = doctorResponse.getBody();

        // Gọi sang patient-service
        String patientUrl = "http://localhost:8080/api/patients/" + appointment.getPatientId();
        ResponseEntity<PatientResponse> patientResponse = restTemplate.exchange(
                patientUrl,
                HttpMethod.GET,
                entity,
                PatientResponse.class
        );
        PatientResponse patient = patientResponse.getBody();

        // Kết hợp kết quả
        AppointmentResponse result = new AppointmentResponse();
        result.setAppointment(appointment);
        result.setDoctor(doctor);
        result.setPatient(patient);

        return result;
    }


    // Tạo lịch hẹn mới
    public Appointment createAppointment(Appointment appointment) {
        if (appointment.getStatus() == null) appointment.setStatus("PENDING");
        if (appointment.getPaymentStatus() == null) appointment.setPaymentStatus("UNPAID");
        if (appointment.getPaid() == null) appointment.setPaid(false);
        return repository.save(appointment);
    }

    // Cập nhật lịch hẹn
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

    // Xóa lịch hẹn
    public void deleteAppointment(Long id) {
        repository.deleteById(id);
    }

    // Lấy lịch hẹn theo bác sĩ
    public List<Appointment> getAppointmentsByDoctor(Integer doctorId) {
        return repository.findByDoctorId(doctorId);
    }

    // Lấy lịch hẹn theo bệnh nhân
    public List<Appointment> getAppointmentsByPatient(Integer patientId) {
        return repository.findByPatientId(patientId);
    }
}
