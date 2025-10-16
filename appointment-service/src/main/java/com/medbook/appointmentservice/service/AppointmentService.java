package com.medbook.appointmentservice.service;

import com.medbook.appointmentservice.model.Appointment;
import com.medbook.appointmentservice.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;

    // Lấy tất cả lịch hẹn
    public List<Appointment> getAllAppointments() {
        return repository.findAll();
    }

    // Lấy lịch hẹn theo ID
    public Appointment getAppointmentById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
    }

    // Tạo lịch hẹn mới
    public Appointment createAppointment(Appointment appointment) {
        if (appointment.getStatus() == null) {
            appointment.setStatus("PENDING");
        }
        if (appointment.getPaymentStatus() == null) {
            appointment.setPaymentStatus("UNPAID");
        }
        if (appointment.getPaid() == null) {
            appointment.setPaid(false);
        }
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
