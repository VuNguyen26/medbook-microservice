package com.medbook.doctor.controller;

import com.medbook.doctor.model.Doctor;
import com.medbook.doctor.repository.DoctorRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorRepository repository;

    public DoctorController(DoctorRepository repository) {
        this.repository = repository;
    }

    // 🔹 Lấy tất cả bác sĩ
    @GetMapping
    public List<Doctor> getAllDoctors() {
        return repository.findAll();
    }

    // 🔹 Lấy bác sĩ theo ID
    @GetMapping("/{id}")
    public Doctor getDoctorById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
    }

    // 🔹 Thêm bác sĩ mới
    @PostMapping
    public Doctor addDoctor(@RequestBody Doctor doctor) {
        return repository.save(doctor);
    }

    // 🔹 Cập nhật bác sĩ theo ID
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
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
    }

    // 🔹 Xóa bác sĩ theo ID
    @DeleteMapping("/{id}")
    public String deleteDoctor(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Doctor not found with id: " + id);
        }
        repository.deleteById(id);
        return "Doctor with id " + id + " has been deleted successfully.";
    }

    // 🔹 Kiểm tra service hoạt động
    @GetMapping("/test")
    public String test() {
        return "Doctor service is working!";
    }
}
