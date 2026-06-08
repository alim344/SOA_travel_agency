package com.example.tour_service.service;

import com.example.tour_service.DTO.TourExecutionDTO;
import com.example.tour_service.model.Tour;
import com.example.tour_service.model.TourExecution;
import com.example.tour_service.model.TourExecutionStatus;
import com.example.tour_service.repo.TourExecutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TourExecutionService {

    @Autowired
    private TourExecutionRepository tourExecutionRepository;

    @Autowired
    private TourService tourService;

    @Transactional
    public TourExecutionDTO startSession(Long touristId, Long tourId){

        // PROVERI JEL TURA KUPLJENA U PURCHASE SERVISU

        TourExecution existing = tourExecutionRepository.findByTouristIdAndTourId(touristId,tourId);

        if(existing != null){
            return new TourExecutionDTO(existing);
        }

        Tour tour = tourService.getById(tourId);
        if(tour == null){
            return null;
        }

        TourExecution tourExecution = new TourExecution();
        tourExecution.setTouristId(touristId);
        tourExecution.setTour(tour);
        tourExecution.setStatus(TourExecutionStatus.ACTIVE);
        tourExecution.setStartTime(LocalDateTime.now());
        tourExecution.setLastActivityDateTime(LocalDateTime.now());
        tourExecutionRepository.save(tourExecution);

        return new TourExecutionDTO(tourExecution);
    }




}
