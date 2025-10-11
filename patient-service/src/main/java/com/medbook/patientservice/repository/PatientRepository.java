package com.medbook.patientservice.repository;

import com.medbook.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
