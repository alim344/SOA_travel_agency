package com.example.tour_service.controller;

import com.example.tour_service.DTO.TourPointDTO;
import com.example.tour_service.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tour")
public class TourController {

    @Autowired
    private TourService tourService;


    @GetMapping("/getAllDtos")
    public ResponseEntity<List<TourPointDTO>> getAllTours(){
        List<TourPointDTO> dtos = tourService.getTourPointDTOS();
        return ResponseEntity.ok().body(dtos);
    }



}
