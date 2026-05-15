package com.example.tour_service.repo;

import com.example.tour_service.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourRepository extends JpaRepository<Tour, Long> {
    List<Tour>  findByAuthorId(Long id);


    Tour getById(Long id);

}
