package com.medbook.doctor.repository;

import com.medbook.doctor.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Lấy tất cả bác sĩ theo chuyên khoa
    List<Doctor> findBySpecialty(String specialty);

    // ⭐ VERY IMPORTANT: Lấy bác sĩ theo email
    Optional<Doctor> findByEmail(String email);
}
