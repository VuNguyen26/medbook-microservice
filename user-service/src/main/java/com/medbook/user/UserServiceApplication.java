package com.medbook.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableDiscoveryClient
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);

        // ĐOẠN TEST ENCODE PASSWORD
        String rawPassword = "123456";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("\n=======================================");
        System.out.println("Raw password: " + rawPassword);
        System.out.println("Encoded password (Spring format):");
        System.out.println(encodedPassword);
        System.out.println("=======================================\n");
    }
}
