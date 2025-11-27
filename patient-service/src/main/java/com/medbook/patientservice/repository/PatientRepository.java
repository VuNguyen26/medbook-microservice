package com.medbook.patientservice.repository;

import com.medbook.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    // 🔹 Tìm bệnh nhân theo email
    Optional<Patient> findByEmail(String email);
}
