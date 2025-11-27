package com.medbook.appointmentservice.repository;

import com.medbook.appointmentservice.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Lấy danh sách lịch hẹn theo bác sĩ
    List<Appointment> findByDoctorId(Integer doctorId);

    // Lấy danh sách lịch hẹn theo bệnh nhân (ID)
    List<Appointment> findByPatientId(Integer patientId);

    // Lấy lịch hẹn theo bệnh nhân (email) -- KHÔNG dùng nữa
    List<Appointment> findByPatientEmail(String patientEmail);

    // Lấy lịch hẹn theo bác sĩ + ngày
    List<Appointment> findByDoctorIdAndAppointmentDate(Integer doctorId, LocalDate appointmentDate);

    // Lấy danh sách đã được đánh giá
    List<Appointment> findByDoctorIdAndRatedTrue(Integer doctorId);

    // ⭐ LẤY DANH SÁCH BỆNH NHÂN THEO DOCTOR ID (DÙNG patientId, KHÔNG DÙNG EMAIL)
    @Query("SELECT a.patientId AS patientId, COUNT(a.id) AS count, MAX(a.appointmentDate) AS lastVisit " +
            "FROM Appointment a " +
            "WHERE a.doctorId = :doctorId " +
            "GROUP BY a.patientId")
    List<Map<String, Object>> findPatientStatsByDoctorId(Integer doctorId);
    List<Appointment> findByPatientIdIsNull();
}
