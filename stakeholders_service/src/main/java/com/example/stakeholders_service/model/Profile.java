package com.example.stakeholders_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "profiles")
@Getter
@Setter
public class Profile {
    @Id
    private Long userId;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String profilePhoto;

    @Column
    private String biography;

    @Column
    private String motto;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
}