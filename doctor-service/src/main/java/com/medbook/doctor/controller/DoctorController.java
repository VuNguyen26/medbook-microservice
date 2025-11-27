package com.medbook.doctor.controller;

import com.medbook.doctor.model.Doctor;
import com.medbook.doctor.repository.DoctorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorRepository repository;

    // Map specialty_id -> tên chuyên khoa
    private static final Map<Long, String> SPECIALTY_MAP = Map.of(
            1L, "đa khoa",
            2L, "phụ khoa",
            3L, "da liễu",
            4L, "nhi",
            5L, "thần kinh",
            6L, "tiêu hóa"
    );

    public DoctorController(DoctorRepository repository) {
        this.repository = repository;
    }

    // =============== GET ALL / FILTER BY SPECIALTY ==================

    @GetMapping
    public List<Doctor> getDoctors(
            @RequestParam(value = "specialty_id", required = false) Long specialtyId
    ) {

        List<Doctor> doctors = repository.findAll();

        if (specialtyId == null) {
            return doctors;
        }

        String specName = SPECIALTY_MAP.get(specialtyId);
        if (specName == null) {
            return List.of();
        }

        return doctors.stream()
                .filter(d -> specName.equalsIgnoreCase(d.getSpecialty()))
                .collect(Collectors.toList());
    }

    // =============== GET BY ID ==================

    @GetMapping("/{id}")
    public Doctor getDoctorById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Doctor not found with id: " + id
                ));
    }

    // =============== GET BY EMAIL (MỚI THÊM) ==================

    @GetMapping("/email/{email}")
    public Doctor getDoctorByEmail(@PathVariable String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Doctor not found with email: " + email
                ));
    }

    // =============== ADD ==================

    @PostMapping
    public Doctor addDoctor(@RequestBody Doctor doctor) {
        return repository.save(doctor);
    }

    // =============== UPDATE ==================

    @PutMapping("/{id}")
    public Doctor updateDoctor(@PathVariable Long id, @RequestBody Doctor updatedDoctor) {
        return repository.findById(id)
                .map(doctor -> {
                    doctor.setName(updatedDoctor.getName());
                    doctor.setEmail(updatedDoctor.getEmail());
                    doctor.setTitle(updatedDoctor.getTitle());
                    doctor.setFee(updatedDoctor.getFee());
                    doctor.setRating(updatedDoctor.getRating());
                    doctor.setPhone(updatedDoctor.getPhone());
                    doctor.setSpecialty(updatedDoctor.getSpecialty());
                    doctor.setExperience(updatedDoctor.getExperience());
                    doctor.setImageUrl(updatedDoctor.getImageUrl());
                    return repository.save(doctor);
                })
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Doctor not found with id: " + id
                ));
    }

    // =============== DELETE ==================

    @DeleteMapping("/{id}")
    public String deleteDoctor(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Doctor not found with id: " + id
            );
        }
        repository.deleteById(id);
        return "Doctor with id " + id + " has been deleted successfully.";
    }

    @GetMapping("/test")
    public String test() {
        return "Doctor service is working!";
    }
}
