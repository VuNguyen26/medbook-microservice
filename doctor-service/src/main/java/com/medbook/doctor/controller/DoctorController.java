package com.medbook.doctor.controller;

import com.medbook.doctor.model.Doctor;
import com.medbook.doctor.repository.DoctorRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")  // ✅ thêm /api để đồng bộ với gateway
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorRepository repository;

    public DoctorController(DoctorRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Doctor> getAllDoctors() {
        return repository.findAll();
    }

    @PostMapping
    public Doctor addDoctor(@RequestBody Doctor doctor) {
        return repository.save(doctor);
    }

    @GetMapping("/test")
    public String test() {
        return "✅ Doctor service is working!";
    }
}
