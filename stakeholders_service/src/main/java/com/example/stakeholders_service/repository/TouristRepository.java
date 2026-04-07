package com.example.stakeholders_service.repository;

import com.example.stakeholders_service.model.Tourist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TouristRepository extends JpaRepository<Tourist, Long> {
}
