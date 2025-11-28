package com.medbook.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medbook.paymentservice.model.Payment;
import com.medbook.paymentservice.model.PaymentStatus;
import com.medbook.paymentservice.repository.PaymentRepository;
import com.medbook.paymentservice.security.JwtAuthenticationFilter;
import com.medbook.paymentservice.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentRepository paymentRepository;

    @MockBean
    private RestTemplate restTemplate;

    // Mock security beans để tránh lỗi khi khởi tạo SecurityFilterChain
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllPayments_shouldReturnOk() throws Exception {
        Payment p = new Payment();
        p.setId(1L);
        p.setAppointmentId(67L);
        p.setPatientId(12L);
        p.setAmount(170000.0);
        p.setStatus(PaymentStatus.COMPLETED);
        p.setTransactionTime(LocalDateTime.now());

        Mockito.when(paymentRepository.findAll()).thenReturn(List.of(p));

        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void createPayment_shouldReturn201() throws Exception {
        Payment input = new Payment();
        input.setAppointmentId(67L);
        input.setPatientId(12L);
        input.setAmount(170000.0);

        Mockito.when(paymentRepository.save(Mockito.any(Payment.class)))
                .thenAnswer(invocation -> {
                    Payment p = invocation.getArgument(0);
                    if (p.getId() == null) {
                        p.setId(1L);
                    }
                    return p;
                });

        Mockito.when(restTemplate.exchange(
                        Mockito.anyString(),
                        Mockito.eq(HttpMethod.PUT),
                        Mockito.any(HttpEntity.class),
                        Mockito.eq(String.class)))
                .thenReturn(ResponseEntity.ok("OK"));

        String body = objectMapper.writeValueAsString(input);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }
}


