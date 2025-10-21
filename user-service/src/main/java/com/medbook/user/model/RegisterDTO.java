package com.medbook.user.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterDTO {
    private String fullname;
    private String password;
    private String email;
    private String phoneNumber;
}
