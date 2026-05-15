package com.example.tour_service.DTO;

import com.example.tour_service.model.TourStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
}
