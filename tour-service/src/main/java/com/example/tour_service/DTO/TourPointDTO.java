package com.example.tour_service.DTO;

import com.example.tour_service.model.KeyPoint;
import com.example.tour_service.model.TourStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;

import java.util.List;

public class TourPointDTO {

    private Long id;

    private String name;
    private String description;
    private int difficulty;
    private String tags;

    private TourStatus status = TourStatus.DRAFT;
    private double price = 0.0;
    private Long authorId;

    private List<KeyPoint> keyPoints;


}
