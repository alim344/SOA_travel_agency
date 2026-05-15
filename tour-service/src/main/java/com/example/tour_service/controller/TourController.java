package com.example.tour_service.controller;

import com.example.tour_service.DTO.TourDTO;
import com.example.tour_service.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tour")
public class TourController {

    @Autowired
    private TourService tourService;

    @PostMapping
    public ResponseEntity<String> createTour(@RequestBody TourDTO dto) {
        tourService.createTour(dto);
        return ResponseEntity.ok("Successful!");
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<TourDTO>> getToursByAuthor(@PathVariable Long authorId) {
        List<TourDTO> response = tourService.getToursByAuthor(authorId);
        return ResponseEntity.ok(response);
    }
}
