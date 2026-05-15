package com.example.tour_service.repo;

import com.example.tour_service.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourRepository extends JpaRepository<Tour, Long> {

    Tour getById(Long id);
}
