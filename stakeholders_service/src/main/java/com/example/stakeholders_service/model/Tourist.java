package com.example.stakeholders_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Tourist extends User{

    @Column
    private String name;
    @Column
    private String lastName;
    @Column
    private String profilePhoto;
    @Column
    private String biography;
    @Column
    private String motto;


}
