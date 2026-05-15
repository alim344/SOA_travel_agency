package com.example.tour_service.service;

import com.example.tour_service.repo.KeyPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeyPointService {

    @Autowired
    private KeyPointRepository keyPointRepository;
}
