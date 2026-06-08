package com.example.tour_service.DTO;

import com.example.tour_service.model.TourStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourDTO {
    private Long id;
    private String name;
    private String description;
    private int difficulty;
    private List<String> tags;
    private TourStatus status;
    private Double price;
    private Long authorId;
    private double totalDistance;
    private Map<String, Integer> durationByTransport;
    private LocalDateTime publishedAt;
    private LocalDateTime archivedAt;
}
