package com.example.tour_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private int difficulty;
    private String tags; //hestegovi

    @Enumerated(EnumType.STRING)
    private TourStatus status = TourStatus.DRAFT;
    private double price = 0.0;
    private Long authorId;

    @OneToMany(cascade = CascadeType.ALL)
    private List<KeyPoint> keyPoints;


}
