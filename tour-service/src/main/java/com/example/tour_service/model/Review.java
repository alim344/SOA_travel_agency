package com.example.tour_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int rating;

    @Column
    private String comment;

    @Column
    private Long touristId;
    @Column
    private String touristName;
    @Column
    private String touristSurname;

    @Column
    private LocalDateTime visitDate;

    @Column
    private LocalDateTime commentDate;

    @ElementCollection
    private List<String> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "tour_id")
    private Tour tour;
}
