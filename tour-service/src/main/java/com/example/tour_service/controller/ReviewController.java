package com.example.tour_service.controller;

import com.example.tour_service.DTO.ReviewDTO;
import com.example.tour_service.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/{tourId}")
    public ResponseEntity<ReviewDTO> addReview(
            @PathVariable Long tourId,
            @RequestBody ReviewDTO request,
            @RequestHeader("X-User-ID") Long currentUserId) {

        ReviewDTO response = reviewService.addReview(tourId, request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{tourId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByTour(@PathVariable Long tourId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByTour(tourId);
        return ResponseEntity.ok(reviews);
    }
}
