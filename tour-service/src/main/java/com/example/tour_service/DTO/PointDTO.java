package com.example.tour_service.DTO;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointDTO {

    private Long id;

    private String name;
    private String description;

    private String imagePath;
    private double latitude;
    private double longitude;

    public PointDTO() {}

}
