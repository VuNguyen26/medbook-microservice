package com.medbook.appointmentservice.repository;

import com.medbook.appointmentservice.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Tìm danh sách lịch hẹn theo bác sĩ
    List<Appointment> findByDoctorId(Integer doctorId);

    // Tìm danh sách lịch hẹn theo bệnh nhân
    List<Appointment> findByPatientId(Integer patientId);
}
