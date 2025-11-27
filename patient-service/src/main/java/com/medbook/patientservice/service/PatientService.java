package com.medbook.patientservice.service;

import com.medbook.patientservice.model.Patient;
import com.medbook.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    // ⭐ NEW: Find patient by email
    public Optional<Patient> getPatientByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public Patient updatePatient(Long id, Patient updatedPatient) {
        return patientRepository.findById(id)
                .map(existing -> {
                    existing.setFullName(updatedPatient.getFullName());
                    existing.setEmail(updatedPatient.getEmail());
                    existing.setPhone(updatedPatient.getPhone());
                    existing.setAddress(updatedPatient.getAddress());
                    existing.setGender(updatedPatient.getGender());
                    existing.setAge(updatedPatient.getAge());
                    return patientRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Patient not found with id " + id));
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}
