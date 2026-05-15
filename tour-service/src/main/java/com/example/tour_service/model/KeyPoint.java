package com.example.tour_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class KeyPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;
    @Column
    private String description;
    @Column
    private String imagePath;
    @Column
    private double latitude; // geografska sirina
    @Column
    private double longitude; // geografska duyina
    @Column
    private Long tourId;
}
