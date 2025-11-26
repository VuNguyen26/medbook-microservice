package com.medbook.appointmentservice.repository;

import com.medbook.appointmentservice.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Lấy danh sách lịch hẹn theo bác sĩ
    List<Appointment> findByDoctorId(Integer doctorId);

    // Lấy danh sách lịch hẹn theo bệnh nhân (ID)
    List<Appointment> findByPatientId(Integer patientId);

    // Lấy lịch hẹn theo bệnh nhân (email)
    List<Appointment> findByPatientEmail(String patientEmail);

    // Lấy lịch hẹn theo bác sĩ + ngày (để kiểm tra slot trống)
    List<Appointment> findByDoctorIdAndAppointmentDate(Integer doctorId, LocalDate appointmentDate);
}
