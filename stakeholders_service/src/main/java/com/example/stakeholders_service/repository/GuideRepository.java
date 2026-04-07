package com.example.stakeholders_service.repository;

import com.example.stakeholders_service.model.Guide;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideRepository extends JpaRepository<Guide, Long> {
}
