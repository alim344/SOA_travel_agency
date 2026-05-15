package com.example.tour_service.controller;

import com.example.tour_service.service.KeyPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/keypoint")
public class KeyPointController {

    @Autowired
    private KeyPointService keyPointService;
}
