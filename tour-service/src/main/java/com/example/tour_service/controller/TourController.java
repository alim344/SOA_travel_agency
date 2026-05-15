package com.example.tour_service.controller;

import com.example.tour_service.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tour")
public class TourController {

    @Autowired
    private TourService tourService;
}
