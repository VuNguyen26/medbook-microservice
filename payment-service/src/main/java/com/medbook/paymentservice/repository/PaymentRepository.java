package com.medbook.paymentservice.repository;

import com.medbook.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByAppointmentId(Long appointmentId);
}
