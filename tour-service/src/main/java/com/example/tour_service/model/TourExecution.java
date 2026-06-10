package com.example.tour_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
public class TourExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long touristId;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TourExecutionStatus status = TourExecutionStatus.ACTIVE;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Column(nullable = false)
    private LocalDateTime lastActivityDateTime;

    @Column
    private double lastLatitude;

    @Column
    private double lastLongitude;

    @ElementCollection
    @CollectionTable(
            name = "tour_execution_completed_keypoints",
            joinColumns = @JoinColumn(name = "execution_id")
    )
    @MapKeyColumn(name = "keypoint_id")
    @Column(name = "completed_at")
    private Map<Long, LocalDateTime> completedKeyPointsWithTime = new HashMap<>();

}
