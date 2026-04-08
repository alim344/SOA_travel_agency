package com.example.stakeholders_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationResponseDTO {

    private String token;


    private Long expiresIn;


    private String role;


    public AuthenticationResponseDTO() {
        this.token = null;
        this.expiresIn = null;
        this.role = null;
    }

    public AuthenticationResponseDTO(String token, long expiresIn, String role) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.role = role;
    }


}
