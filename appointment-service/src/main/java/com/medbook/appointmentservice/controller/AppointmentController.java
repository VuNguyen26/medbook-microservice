package com.medbook.appointmentservice.controller;

import com.medbook.appointmentservice.model.Appointment;
import com.medbook.appointmentservice.repository.AppointmentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentRepository repository;

    public AppointmentController(AppointmentRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Appointment> getAllAppointments() {
        return repository.findAll();
    }

    @PostMapping
    public Appointment createAppointment(@RequestBody Appointment appointment) {
        return repository.save(appointment);
    }

    @GetMapping("/{id}")
    public Appointment getAppointmentById(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteAppointment(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
