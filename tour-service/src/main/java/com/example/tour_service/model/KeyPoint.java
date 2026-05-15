package com.example.tour_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "tour_id")
    private Tour tour;
}
