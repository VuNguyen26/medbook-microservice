package com.medbook.appointmentservice.service;

import com.medbook.appointmentservice.model.Appointment;
import com.medbook.appointmentservice.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AppointmentService service;

    @Test
    void markAsPaid_shouldUpdatePaymentStatusAndPaidFlag() {
        Appointment a = new Appointment();
        a.setId(1L);
        a.setPaymentStatus("UNPAID");
        a.setPaid(false);

        when(repository.findById(1L)).thenReturn(Optional.of(a));
        when(repository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Appointment result = service.markAsPaid(1L);

        assertEquals("PAID", result.getPaymentStatus());
        assertTrue(result.getPaid());
    }
}


