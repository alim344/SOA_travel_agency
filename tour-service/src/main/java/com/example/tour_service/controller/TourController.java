package com.example.tour_service.controller;

import com.example.tour_service.DTO.DurationRequest;
import com.example.tour_service.DTO.TourDTO;
import com.example.tour_service.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.example.tour_service.DTO.TourPointDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/{id}")
    public ResponseEntity<TourDTO> getTourById(@PathVariable Long id) {
        TourDTO response = tourService.getTourById(id);
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

    @GetMapping("/getAllActiveTours")
    public ResponseEntity<List<TourPointDTO>> getAllActiveTours(){
        List<TourPointDTO> dtos = tourService.getActiveTourPointDTOS();
        return ResponseEntity.ok().body(dtos);
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<TourDTO> publishTour(@PathVariable Long id) {
        TourDTO response = tourService.publishTour(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/duration")
    public ResponseEntity<TourDTO> addDuration(
            @PathVariable Long id,
            @RequestBody DurationRequest request) {

        TourDTO response = tourService.addDuration(id, request.getTransportType(), request.getMinutes());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<TourDTO> archiveTour(@PathVariable Long id) {
        TourDTO response = tourService.archiveTour(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reactivate")
    public ResponseEntity<TourDTO> reactivateTour(@PathVariable Long id) {
        TourDTO response = tourService.reactivateTour(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/price")
    public ResponseEntity<TourDTO> updateTourPrice(@PathVariable Long id, @RequestBody Map<String, Double> request) {
        Double newPrice = request.get("price");
        TourDTO response = tourService.updateTourPrice(id, newPrice);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/archiveByUser/{userId}")
    public ResponseEntity<String> archiveToursByUser(@PathVariable Long userId) {
        try {
            int archivedCount = tourService.archiveToursByAuthor(userId);
            if (archivedCount == 0) {
                return ResponseEntity.ok("No tours found for user " + userId + " to archive");
            }
            return ResponseEntity.ok("Successfully archived " + archivedCount + " tours for user " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to archive tours: " + e.getMessage());
        }
    }

}
