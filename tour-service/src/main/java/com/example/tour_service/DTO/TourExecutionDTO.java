package com.example.tour_service.DTO;

import com.example.tour_service.model.Tour;
import com.example.tour_service.model.TourExecution;
import com.example.tour_service.model.TourExecutionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class TourExecutionDTO {

    private Long id;


    private Long touristId;


    private Long tourId;


    private String status;


    private LocalDateTime startTime;


    private LocalDateTime endTime;


    private LocalDateTime lastActivityDateTime;

    @Column
    private double lastLatitude;

    @Column
    private double lastLongitude;

    public TourExecutionDTO() {}

    public TourExecutionDTO(TourExecution tourExecution) {
        this.id = tourExecution.getId();
        this.touristId = tourExecution.getTouristId();
        this.endTime = tourExecution.getEndTime();
        this.startTime = tourExecution.getStartTime();
        this.lastActivityDateTime = tourExecution.getLastActivityDateTime();
        this.lastLatitude = tourExecution.getLastLatitude();
        this.lastLongitude = tourExecution.getLastLongitude();
        this.status = tourExecution.getStatus().toString();
        this.tourId = tourExecution.getTour().getId();
    }





}
