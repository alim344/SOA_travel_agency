package com.example.stakeholders_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequestDTO {

    private String email;
    private String password;



    public AuthenticationRequestDTO(String email, String password) {
        this.setEmail(email);
        this.setPassword(password);
    }
}
