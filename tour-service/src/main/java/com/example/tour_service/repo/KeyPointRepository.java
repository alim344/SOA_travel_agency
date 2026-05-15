package com.example.tour_service.repo;

import com.example.tour_service.model.KeyPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeyPointRepository extends JpaRepository<KeyPoint, Long> {
    List<KeyPoint> findKeyPointByTourId(Long tourId);

}
