package com.example.tour_service.DTO;

import com.example.tour_service.model.KeyPoint;
import com.example.tour_service.model.TourStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TourPointDTO {

    private Long id;

    private String name;

    private String description;

    private int difficulty;

    private String tags;


    private String status ;
    private double price;
    private Long authorId;

    public TourPointDTO() {}

}
