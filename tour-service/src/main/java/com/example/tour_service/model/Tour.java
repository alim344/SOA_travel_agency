package com.example.tour_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.MapKeyEnumerated;

import java.util.List;

@Getter
@Setter
@Entity
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private int difficulty;
    @Column
    private String tags;
    @Column
    private Double totalDistance;
    @Column
    private LocalDateTime publishedAt;
    @Column
    private LocalDateTime archivedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TourStatus status = TourStatus.DRAFT;
    @Column
    private double price = 0.0;
    @Column(nullable = false)
    private Long authorId;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KeyPoint> keyPoints;

    @ElementCollection
    @MapKeyEnumerated(EnumType.STRING)
    private Map<TransportType, Integer> durationByTransport = new HashMap<>();
}
