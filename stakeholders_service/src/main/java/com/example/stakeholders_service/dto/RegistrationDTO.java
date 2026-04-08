package com.example.stakeholders_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationDTO {

    private String username;
    private String email;
    private String password;
    private String role;

    private String firstName;
    private String lastName;
    private String motto;



    public RegistrationDTO( String email, String password, String username, String role) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
    }

    public RegistrationDTO() {}


}
