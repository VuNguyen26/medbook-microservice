package com.medbook.doctor.repository;

import com.medbook.doctor.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // 🔹 Lấy tất cả bác sĩ theo chuyên khoa
    List<Doctor> findBySpecialty(String specialty);
}
