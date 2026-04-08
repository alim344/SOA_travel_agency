package com.example.stakeholders_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDTO {

    private Long id;
    private String username;
    private String email;
    private String role;

    private String firstName;
    private String lastName;
    private String motto;

    private boolean active;
    public AccountDTO() {}


}
