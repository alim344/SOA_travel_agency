package com.example.tour_service.controller;

import com.example.tour_service.DTO.TourDTO;
import com.example.tour_service.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.example.tour_service.DTO.TourPointDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tour")
public class TourController {

    @Autowired
    private TourService tourService;

    @PostMapping
    public ResponseEntity<String> createTour(
            @RequestBody TourDTO dto,
            @RequestHeader(value = "X-User-ID", required = false) Long userIdFromHeader) {

        Long authorId = userIdFromHeader != null ? userIdFromHeader : dto.getAuthorId();

        tourService.createTour(dto, authorId);
        return ResponseEntity.ok("Successful!");
    }

    @GetMapping("/author")
    public ResponseEntity<List<TourDTO>> getToursByAuthor(@RequestHeader("X-User-ID") Long authorId) {
        List<TourDTO> response = tourService.getToursByAuthor(authorId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/nodraft/guide/{authorId}")
    public ResponseEntity<List<TourDTO>> getGuideNoDraftTours(@PathVariable Long authorId) {
        List<TourDTO> response = tourService.GetNoDraftTours(authorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllDtos")
    public ResponseEntity<List<TourPointDTO>> getAllTours(){
        List<TourPointDTO> dtos = tourService.getTourPointDTOS();
        return ResponseEntity.ok().body(dtos);
    }



}
