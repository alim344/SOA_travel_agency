package com.example.tour_service.repo;

import com.example.tour_service.model.TourExecution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourExecutionRepository extends JpaRepository<TourExecution, Long> {

    //boolean existsByTouristIdAndTourId(Long touristId, Long tourId);
    TourExecution findByTouristIdAndTourId(Long touristId, Long tourId);
}
