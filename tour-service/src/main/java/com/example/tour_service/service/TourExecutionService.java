package com.example.tour_service.service;

import com.example.tour_service.DTO.LocationDTO;
import com.example.tour_service.DTO.TourExecutionDTO;
import com.example.tour_service.model.KeyPoint;
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

    @Transactional
    public TourExecutionDTO abandonTour(Long executionId){
        TourExecution tourExecution = tourExecutionRepository.findById(executionId).orElse(null);
        if(tourExecution == null){
            return null;
        }

        if(tourExecution.getStatus() != TourExecutionStatus.ACTIVE){
            return null;
        }

        tourExecution.setStatus(TourExecutionStatus.ABANDONED);
        tourExecution.setLastActivityDateTime(LocalDateTime.now());
        tourExecution.setEndTime(LocalDateTime.now());
        tourExecutionRepository.save(tourExecution);
        return new TourExecutionDTO(tourExecution);

    }

    @Transactional
    public TourExecutionDTO checkPosition(Long executionId,LocationDTO dto){

        TourExecution execution = tourExecutionRepository.findById(executionId).orElse(null);
        if(execution == null){
            return null;
        }

        execution.setLastLatitude(dto.getLatitude());
        execution.setLastLongitude(dto.getLongitude());
        execution.setLastActivityDateTime(LocalDateTime.now());

        Tour tour = tourService.getById(execution.getTour().getId());
        for(KeyPoint kp: tour.getKeyPoints()){
            if (!execution.getCompletedKeyPointsWithTime().containsKey(kp.getId())) {

                double distance = calculateDistance(dto.getLatitude(), dto.getLongitude(), kp.getLatitude(), kp.getLongitude());

                if (distance <= 50.0) {
                    execution.getCompletedKeyPointsWithTime().put(kp.getId(), LocalDateTime.now());
                }
            }

        }

        if (execution.getCompletedKeyPointsWithTime().size() == tour.getKeyPoints().size()) {
            execution.setStatus(TourExecutionStatus.COMPLETED);
            execution.setEndTime(LocalDateTime.now());
        }
        TourExecution exec = tourExecutionRepository.save(execution);
        return new TourExecutionDTO(exec);
    }


    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371e3; //rad zemlje
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }


}
