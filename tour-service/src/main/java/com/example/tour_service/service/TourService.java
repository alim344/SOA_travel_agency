package com.example.tour_service.service;

import com.example.tour_service.repo.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TourService {

    @Autowired
    private TourRepository tourRepository;
}
