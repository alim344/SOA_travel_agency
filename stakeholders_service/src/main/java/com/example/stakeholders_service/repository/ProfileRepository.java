package com.example.stakeholders_service.repository;

import com.example.stakeholders_service.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Profile findByUserId(Long userId);
}
